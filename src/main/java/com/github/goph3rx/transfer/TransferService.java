package com.github.goph3rx.transfer;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for world transfers. */
public class TransferService implements ITransferService {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
  /** How long the transfer token is valid for. */
  private static final int TOKEN_VALIDITY_MINUTES = 10;

  /** Secure random number generation. */
  private final SecureRandom random = new SecureRandom();

  /** Adapter for the database. */
  @Inject private ITransferDatabase database;

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
}
