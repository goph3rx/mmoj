package com.github.goph3rx.account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing accounts. */
public class AccountService implements IAccountService {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
  /** Name of the hashing algorithm in use. */
  private static final String ALGORITHM = "SHA-256";

  /** Adapter for the database. */
  @Inject private IAccountDatabase database;

  @Override
  public Optional<Account> find(String username, String password) {
    logger.debug("Finding account with username='{}'", username);

    // Load the record
    var result = database.fetch(username);
    if (result.isEmpty()) {
      logger.warn("Account with username='{}' not found", username);
      return Optional.empty();
    }

    // Check password
    var account = result.get();
    var salt = Base64.getDecoder().decode(account.salt());
    var actual = computeHash(salt, password);
    var expected = Base64.getDecoder().decode(account.password());
    if (!MessageDigest.isEqual(expected, actual)) {
      logger.warn("Account with username='{}' got password mismatch", username);
      return Optional.empty();
    }

    // Return result
    logger.debug("Account is {}", account);
    return result;
  }

  /**
   * Compute the password hash.
   *
   * @param salt Randomly chosen salt.
   * @param password Password.
   * @return Password hash.
   */
  private byte[] computeHash(byte[] salt, String password) {
    try {
      var digest = MessageDigest.getInstance(ALGORITHM);
      digest.update(salt);
      digest.update(password.getBytes());
      return digest.digest();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
