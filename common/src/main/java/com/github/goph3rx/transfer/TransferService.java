package com.github.goph3rx.transfer;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for world transfers. */
public class TransferService implements ITransferService {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
  /** How long the transfer token is valid for. */
  private static final int TOKEN_VALIDITY_MINUTES = 10;
  /** How often to clean up the expired tokens. */
  private static final int TOKEN_CLEANUP_SECONDS = 10;
  /** Secure random number generation. */
  private static final SecureRandom random = new SecureRandom();
  /** Executor for scheduled tasks originating from this class. */
  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());

  /** Adapter for the database. */
  @Inject public ITransferDatabase database;

  /** Create a new service. */
  @Inject
  public TransferService() {
    // Constructor is required for DI
  }

  @Override
  public Transfer generate(String account) {
    logger.debug("Generating transfer for account='{}'", account);

    // Generate the token
    var auth = random.nextLong();
    var play = random.nextLong();
    var expiry = LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES);
    var transfer = new Transfer(account, auth, play, expiry);

    // Persist it and return results
    database.create(transfer);
    logger.debug("Transfer is {}", transfer);
    return transfer;
  }

  @Override
  public void start() {
    executor.scheduleAtFixedRate(
        () -> {
          try {
            var total = database.removeExpired();
            if (total > 0) {
              logger.info("Cleaned up {} expired transfer(s)", total);
            }
          } catch (Exception e) {
            logger.warn("Failed to clean up expired transfers", e);
          }
        },
        0,
        TOKEN_CLEANUP_SECONDS,
        TimeUnit.SECONDS);
  }
}
