package com.github.goph3rx.transfer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.inject.Inject;
import javax.inject.Named;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adapter for the database that holds transfer information. */
public class TransferDatabase implements ITransferDatabase {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(TransferDatabase.class);

  /** Database. */
  private final Jdbi db;

  /**
   * Create a new database adapter.
   *
   * @param jdbcUrl Connection URL.
   */
  @Inject
  public TransferDatabase(@Named("transfer.db") String jdbcUrl) {
    var config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setPoolName("transfers");
    db = Jdbi.create(new HikariDataSource(config));
    db.registerRowMapper(ConstructorMapper.factory(Transfer.class));
  }

  @Override
  public void create(Transfer transfer) {
    logger.debug("Creating transfer {}", transfer);
    db.useHandle(
        handle ->
            handle
                .createUpdate(
                    "INSERT INTO transfers (account, auth, play, expiry) VALUES (?, ?, ?, ?) ON CONFLICT (account) DO UPDATE SET auth = EXCLUDED.auth, play = EXCLUDED.play, expiry = EXCLUDED.expiry")
                .bind(0, transfer.account())
                .bind(1, transfer.auth())
                .bind(2, transfer.play())
                .bind(3, transfer.expiry())
                .execute());
    logger.debug("Success");
  }
}
