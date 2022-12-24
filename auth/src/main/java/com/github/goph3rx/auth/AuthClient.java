package com.github.goph3rx.auth;

import com.github.goph3rx.account.Account;
import com.github.goph3rx.account.IAccountService;
import com.github.goph3rx.auth.messages.*;
import com.github.goph3rx.transfer.ITransferService;
import com.github.goph3rx.transfer.Transfer;
import com.github.goph3rx.world.IWorldService;
import com.github.goph3rx.world.World;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Client connected to the server for auth. */
public class AuthClient implements Runnable {
  /** Position of username data in the credential blob. */
  private static final int NAME_OFFSET = 94;
  /** Maximum length of the username. */
  private static final int NAME_SIZE = 14;
  /** Position of password data in the credential blob. */
  private static final int PASSWORD_OFFSET = 108;
  /** Maximum length of the password. */
  private static final int PASSWORD_SIZE = 16;
  /** How long to wait for client to authenticate. */
  private static final int AUTH_TIMEOUT_SECONDS = 5;
  /** How long to wait for client to enter the world. */
  private static final int IDLE_TIMEOUT_MINUTES = 10;
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(AuthClient.class);
  /** Executor for scheduled tasks originating from this class. */
  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());

  /** Connection with the client. */
  private final IAuthConnection connection;
  /** Key for credential encryption. */
  @Inject public IAuthCredentialKey credentialKey;
  /** Manager for clients connected to the server. */
  @Inject public IAuthClientManager clientManager;
  /** Service for managing accounts. */
  @Inject public IAccountService accounts;
  /** Service for managing world transfers. */
  @Inject public ITransferService transfers;
  /** Service for managing registered worlds. */
  @Inject public IWorldService worlds;

  /** Account for this client (if logged in). */
  private Optional<Account> account = Optional.empty();
  /** Transfer for this client (if logged in). */
  private Optional<Transfer> transfer = Optional.empty();

  /** Get the account username associated with this client. */
  public String getUsername() {
    return account.map(Account::username).orElse("");
  }

  /**
   * Set the new account for the client. Must only be used in tests.
   *
   * @param account Account.
   */
  public void setAccount(Account account) {
    this.account = Optional.of(account);
  }

  /**
   * Set the new transfer for the client. Must only be used in tests.
   *
   * @param transfer Transfer.
   */
  public void setTransfer(Transfer transfer) {
    this.transfer = Optional.of(transfer);
  }

  /**
   * Create a new client.
   *
   * @param connection Connection with the client.
   */
  public AuthClient(IAuthConnection connection) {
    this.connection = connection;
  }

  @Override
  public void run() {
    init();
    receive();
    disconnect();
  }

  /** Initialize the client. */
  public void init() {
    try {
      MDC.put("remote", connection.getRemoteAddress().toString());
      logger.info("New client");
      var start = Instant.now();

      // Generate the encryption key
      var random = new SecureRandom();
      var cryptKey = new byte[16];
      random.nextBytes(cryptKey);

      // Initialize the client
      var message = new ServerInit(0xdeadbeef, credentialKey.getModulus(), cryptKey);
      send(message);
      clientManager.add(this);

      // Disconnect the client if it goes inactive
      executor.schedule(this::assertAuth, AUTH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
      executor.schedule(this::disconnect, IDLE_TIMEOUT_MINUTES, TimeUnit.MINUTES);

      logger.debug("Took {}ms", Duration.between(start, Instant.now()).toMillis());
    } catch (Exception e) {
      logger.error("Failed to init client", e);
    }
  }

  /** Check that client is authenticated and disconnect it otherwise. */
  private void assertAuth() {
    if (!getUsername().isEmpty()) {
      return;
    }

    disconnect();
  }

  /** Receive and process messages. */
  private void receive() {
    logger.debug("Waiting for message");
    try {
      while (true) {
        var message = connection.receive();
        if (message.isEmpty()) {
          return;
        }
        handle(message.get());
      }
    } catch (Exception e) {
      logger.error("Unexpected error whilst processing", e);
    }
  }

  /**
   * Send a message to the client.
   *
   * @param message Message.
   * @throws IOException Unable to send response to client.
   */
  public void send(Object message) throws IOException {
    connection.send(message);
  }

  /**
   * Handle a single message.
   *
   * @param message Message.
   * @throws IOException Unable to send response to client.
   */
  public void handle(Object message) throws IOException {
    var start = Instant.now();
    switch (message) {
      case ClientAuthGameGuard m -> handle(m);
      case ClientRequestAuthLogin m -> handle(m);
      case ClientRequestServerList m -> handle(m);
      case ClientRequestServerLogin m -> handle(m);
      default -> throw new IllegalArgumentException("Cannot handle this message type");
    }
    logger.debug("Took {}ms", Duration.between(start, Instant.now()).toMillis());
  }

  @SuppressWarnings("unused")
  private void handle(ClientAuthGameGuard message) throws IOException {
    send(new ServerGGAuth(GGAuthResult.SKIP));
  }

  private void handle(ClientRequestAuthLogin message) throws IOException {
    // Decrypt and extract the credentials
    var credentials = message.credentials();
    credentialKey.decrypt(credentials);
    var username =
        new String(credentials, NAME_OFFSET, NAME_SIZE, StandardCharsets.ISO_8859_1).trim();
    var password =
        new String(credentials, PASSWORD_OFFSET, PASSWORD_SIZE, StandardCharsets.ISO_8859_1).trim();

    // Find the account
    Account myAccount;
    try {
      var result = accounts.find(username, password);
      if (result.isEmpty()) {
        logger.warn("Invalid credentials for username='{}'", username);
        send(new ServerLoginFail(LoginFailReason.USER_OR_PASS_WRONG));
        return;
      }
      myAccount = result.get();
    } catch (Exception e) {
      send(new ServerLoginFail(LoginFailReason.SYSTEM_ERROR));
      logger.error("Failed to find account", e);
      return;
    }

    // Check the account status
    var isBanned =
        myAccount.bannedUntil().map(value -> value.isBefore(LocalDateTime.now())).orElse(false);
    if (Boolean.TRUE.equals(isBanned)) {
      logger.warn("Account is banned username='{}' until={}", username, myAccount.bannedUntil());
      send(new ServerAccountKicked(AccountKickedReason.PERMANENTLY_BANNED));
      return;
    }

    // Same account is only allowed to connect once
    var duplicate = clientManager.find(username);
    if (duplicate.isPresent()) {
      logger.warn("Account in use username='{}'", username);
      var fail = new ServerLoginFail(LoginFailReason.ACCOUNT_IN_USE);
      send(fail);
      duplicate.get().send(fail);
      return;
    }

    // Generate the token for world transfer
    Transfer myTransfer;
    try {
      myTransfer = transfers.generate(username);
    } catch (Exception e) {
      send(new ServerLoginFail(LoginFailReason.SYSTEM_ERROR));
      logger.error("Failed to generate transfer", e);
      return;
    }

    // Success
    MDC.put("account", myAccount.username());
    logger.info("Account logged in");
    account = Optional.of(myAccount);
    transfer = Optional.of(myTransfer);
    send(new ServerLoginOk(myTransfer.auth()));
  }

  private void handle(ClientRequestServerList message) throws IOException {
    var myTransfer = transfer.orElseThrow();
    var myAccount = account.orElseThrow();

    // Check the token
    if (myTransfer.auth() != message.authToken()) {
      logger.warn("Invalid token server={} client={}", myTransfer.auth(), message.authToken());
      send(new ServerLoginFail(LoginFailReason.ACCESS_FAILED));
      return;
    }

    // Show the list of worlds
    List<World> list;
    try {
      list = worlds.list();
    } catch (Exception e) {
      send(new ServerLoginFail(LoginFailReason.SYSTEM_ERROR));
      logger.error("Failed to list worlds", e);
      return;
    }
    send(new ServerServerList(myAccount.lastWorld(), list));
  }

  private void handle(ClientRequestServerLogin message) throws IOException {
    var myTransfer = transfer.orElseThrow();
    var myAccount = account.orElseThrow();

    // Check the token
    if (myTransfer.auth() != message.authToken()) {
      logger.warn("Invalid token server={} client={}", myTransfer.auth(), message.authToken());
      send(new ServerPlayFail(LoginFailReason.ACCESS_FAILED));
      return;
    }

    // Update the last world entered
    try {
      accounts.setLastWorld(myAccount.username(), message.worldId());
    } catch (Exception e) {
      logger.warn("Failed to set the last world", e);
    }

    // Transfer to the world
    send(new ServerPlayOk(myTransfer.play()));
    logger.info("Account entering world id={}", message.worldId());
  }

  /** Disconnect the client. */
  private void disconnect() {
    logger.info("Disconnecting client");
    connection.close();
    clientManager.remove(this);
  }
}
