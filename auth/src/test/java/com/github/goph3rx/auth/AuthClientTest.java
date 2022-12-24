package com.github.goph3rx.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

import com.github.goph3rx.account.Account;
import com.github.goph3rx.account.IAccountService;
import com.github.goph3rx.auth.messages.*;
import com.github.goph3rx.transfer.ITransferService;
import com.github.goph3rx.transfer.Transfer;
import com.github.goph3rx.world.IWorldService;
import com.github.goph3rx.world.World;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.Test;
import org.mockito.*;

public class AuthClientTest extends MockitoTest {
  private static final byte[] CREDENTIALS =
      HexFormat.of()
          .parseHex(
              "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000068656C6C6F000000000000000000776F726C64000000000000000000000000000000");
  private static final Account ACCOUNT = new Account("hello", "", "", 1, Optional.empty());
  private static final Transfer TRANSFER = new Transfer("hello", 123, 456, LocalDateTime.now());

  @Mock private IAuthConnection connection;
  @Mock private IAuthCredentialKey credentialKey;
  @Mock private IAuthClientManager clientManager;
  @Mock private IAccountService accounts;
  @Mock private ITransferService transfers;
  @Mock private IWorldService worlds;
  @InjectMocks private AuthClient client;

  @Captor private ArgumentCaptor<Object> response;

  @Test
  public void initFail() {
    // Given
    when(connection.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
    when(credentialKey.getModulus()).thenThrow(new RuntimeException());

    // When/Then
    client.init();
  }

  @Test
  public void initSuccess() throws IOException {
    // Given
    when(connection.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
    when(credentialKey.getModulus()).thenReturn(new byte[128]);

    // When
    client.init();

    // Then
    verify(connection).send(response.capture());
    var init = (ServerInit) response.getValue();
    assertEquals(0xdeadbeef, init.sessionId());
    assertEquals(128, init.modulus().length);
    assertEquals(16, init.cryptKey().length);
  }

  @Test(expected = IllegalArgumentException.class)
  public void handleInvalid() throws IOException {
    // Given
    var request = new Object();

    // When/Then
    client.handle(request);
  }

  @Test
  public void handleAuthGameGuard() throws IOException {
    // Given
    var request = new ClientAuthGameGuard();

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var ggAuth = (ServerGGAuth) response.getValue();
    assertEquals(GGAuthResult.SKIP, ggAuth.result());
  }

  @Test
  public void handleRequestAuthLoginAccountError() throws IOException {
    // Given
    var request = new ClientRequestAuthLogin(CREDENTIALS);
    when(accounts.find("hello", "world")).thenThrow(new RuntimeException("DB Fail"));

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var loginFail = (ServerLoginFail) response.getValue();
    assertEquals(LoginFailReason.SYSTEM_ERROR, loginFail.reason());
  }

  @Test
  public void handleRequestAuthLoginCredentialsError() throws IOException {
    // Given
    var request = new ClientRequestAuthLogin(CREDENTIALS);
    when(accounts.find("hello", "world")).thenReturn(Optional.empty());

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var loginFail = (ServerLoginFail) response.getValue();
    assertEquals(LoginFailReason.USER_OR_PASS_WRONG, loginFail.reason());
  }

  @Test
  public void handleRequestAuthLoginBanned() throws IOException {
    // Given
    var request = new ClientRequestAuthLogin(CREDENTIALS);
    when(accounts.find("hello", "world"))
        .thenReturn(
            Optional.of(
                new Account("hello", "", "", 1, Optional.of(LocalDateTime.now().minusMinutes(1)))));

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var accountKicked = (ServerAccountKicked) response.getValue();
    assertEquals(AccountKickedReason.PERMANENTLY_BANNED, accountKicked.reason());
  }

  @Test
  public void handleRequestAuthLoginDuplicate() throws IOException {
    // Given
    var request = new ClientRequestAuthLogin(CREDENTIALS);
    when(accounts.find("hello", "world")).thenReturn(Optional.of(ACCOUNT));
    var duplicate = mock(AuthClient.class);
    when(clientManager.find("hello")).thenReturn(Optional.of(duplicate));

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var loginFail = (ServerLoginFail) response.getValue();
    assertEquals(LoginFailReason.ACCOUNT_IN_USE, loginFail.reason());
    verify(duplicate).send(response.capture());
    loginFail = (ServerLoginFail) response.getValue();
    assertEquals(LoginFailReason.ACCOUNT_IN_USE, loginFail.reason());
  }

  @Test
  public void handleRequestAuthLoginTransferError() throws IOException {
    // Given
    var request = new ClientRequestAuthLogin(CREDENTIALS);
    when(accounts.find("hello", "world")).thenReturn(Optional.of(ACCOUNT));
    when(clientManager.find("hello")).thenReturn(Optional.empty());
    when(transfers.generate("hello")).thenThrow(new RuntimeException("DB Fail"));

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var loginFail = (ServerLoginFail) response.getValue();
    assertEquals(LoginFailReason.SYSTEM_ERROR, loginFail.reason());
  }

  @Test
  public void handleRequestAuthLoginSuccess() throws IOException {
    // Given
    var request = new ClientRequestAuthLogin(CREDENTIALS);
    when(accounts.find("hello", "world")).thenReturn(Optional.of(ACCOUNT));
    when(clientManager.find("hello")).thenReturn(Optional.empty());
    when(transfers.generate("hello")).thenReturn(TRANSFER);

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var loginOk = (ServerLoginOk) response.getValue();
    assertEquals(123, loginOk.authToken());
  }

  @Test(expected = NoSuchElementException.class)
  public void handleRequestServerListInvalidState() throws IOException {
    // Given
    var request = new ClientRequestServerList(123);

    // When/Then
    client.handle(request);
  }

  @Test
  public void handleRequestServerListInvalidToken() throws IOException {
    // Given
    client.setAccount(ACCOUNT);
    client.setTransfer(TRANSFER);
    var request = new ClientRequestServerList(999);

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var loginFail = (ServerLoginFail) response.getValue();
    assertEquals(LoginFailReason.ACCESS_FAILED, loginFail.reason());
  }

  @Test
  public void handleRequestServerListSystemError() throws IOException {
    // Given
    client.setAccount(ACCOUNT);
    client.setTransfer(TRANSFER);
    var request = new ClientRequestServerList(TRANSFER.auth());
    when(worlds.list()).thenThrow(new RuntimeException("DB Fail"));

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var loginFail = (ServerLoginFail) response.getValue();
    assertEquals(LoginFailReason.SYSTEM_ERROR, loginFail.reason());
  }

  @Test
  public void handleRequestServerListSuccess() throws IOException {
    // Given
    client.setAccount(ACCOUNT);
    client.setTransfer(TRANSFER);
    var request = new ClientRequestServerList(TRANSFER.auth());
    var expected = List.of(new World(1, Inet4Address.getLoopbackAddress(), 7777, 0, 1000, true));
    when(worlds.list()).thenReturn(expected);

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var serverList = (ServerServerList) response.getValue();
    assertSame(expected, serverList.worlds());
    assertEquals(ACCOUNT.lastWorld(), serverList.lastWorld());
  }

  @Test(expected = NoSuchElementException.class)
  public void handleRequestServerLoginInvalidState() throws IOException {
    // Given
    var request = new ClientRequestServerLogin(TRANSFER.auth(), 1);

    // When/Then
    client.handle(request);
  }

  @Test
  public void handleRequestServerLoginInvalidToken() throws IOException {
    // Given
    client.setAccount(ACCOUNT);
    client.setTransfer(TRANSFER);
    var request = new ClientRequestServerLogin(999, 1);

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var playFail = (ServerPlayFail) response.getValue();
    assertEquals(LoginFailReason.ACCESS_FAILED, playFail.reason());
  }

  @Test
  public void handleRequestServerLoginUpdateFailIgnored() throws IOException {
    // Given
    client.setAccount(ACCOUNT);
    client.setTransfer(TRANSFER);
    var request = new ClientRequestServerLogin(TRANSFER.auth(), 2);
    doThrow(new RuntimeException("DB Fail")).when(accounts).setLastWorld("hello", 2);

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var playOk = (ServerPlayOk) response.getValue();
    assertEquals(TRANSFER.play(), playOk.playToken());
  }

  @Test
  public void handleRequestServerLoginSuccess() throws IOException {
    // Given
    client.setAccount(ACCOUNT);
    client.setTransfer(TRANSFER);
    var request = new ClientRequestServerLogin(TRANSFER.auth(), 2);

    // When
    client.handle(request);

    // Then
    verify(connection).send(response.capture());
    var playOk = (ServerPlayOk) response.getValue();
    assertEquals(TRANSFER.play(), playOk.playToken());
  }
}
