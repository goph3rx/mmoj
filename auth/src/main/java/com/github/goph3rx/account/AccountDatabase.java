package com.github.goph3rx.account;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adapter for the database that holds account information. */
public class AccountDatabase implements IAccountDatabase {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(AccountDatabase.class);

  /** Database. */
  private final Jdbi db;

  /**
   * Create a new database adapter.
   *
   * @param jdbcUrl Connection URL.
   */
  @Inject
  public AccountDatabase(@Named("account.db") String jdbcUrl) {
    var config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setPoolName("accounts");
    db = Jdbi.create(new HikariDataSource(config));
    db.registerRowMapper(ConstructorMapper.factory(Account.class));
  }

  @Override
  public Optional<Account> fetch(String username) {
    logger.debug("Fetching account with username='{}'", username);
    var account =
        db.withHandle(
            handle ->
                handle
                    .createQuery(
                        "SELECT username, salt, password, last_world, banned_until FROM accounts WHERE username = ? LIMIT 1")
                    .bind(0, username)
                    .mapTo(Account.class)
                    .findOne());
    logger.debug("Account is {}", account);
    return account;
  }

  @Override
  public void setLastWorld(String username, int lastWorld) {
    logger.debug("Setting last world username='{}' lastWorld={}", username, lastWorld);
    db.useHandle(
        handle ->
            handle
                .createUpdate("UPDATE accounts SET last_world = ? WHERE username = ?")
                .bind(0, lastWorld)
                .bind(1, username)
                .execute());
    logger.debug("Success");
  }
}
