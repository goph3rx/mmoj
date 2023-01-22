package com.github.goph3rx.character;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adapter for the database that holds general character information. */
public class CharacterDatabase implements ICharacterDatabase {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(CharacterDatabase.class);

  /** Database. */
  private final Jdbi db;

  /**
   * Create a new database adapter.
   *
   * @param jdbcUrl Connection URL.
   */
  @Inject
  public CharacterDatabase(@Named("character.db") String jdbcUrl) {
    var config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setPoolName("characters");
    db = Jdbi.create(new HikariDataSource(config));
    db.registerRowMapper(
        Character.class,
        (rs, ctx) ->
            new Character(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("account"),
                CharacterRace.valueOf(rs.getInt("race")),
                rs.getInt("class"),
                CharacterGender.valueOf(rs.getInt("gender")),
                HexFormat.of().parseHex(rs.getString("appearance")),
                rs.getTimestamp("created_on").toLocalDateTime(),
                Optional.ofNullable(rs.getTimestamp("delete_on")).map(Timestamp::toLocalDateTime),
                Optional.ofNullable(rs.getTimestamp("last_used_on"))
                    .map(Timestamp::toLocalDateTime)));
  }

  @Override
  public void create(Character character) throws CharacterDuplicateException {
    logger.debug("Creating character {}", character);
    try {
      db.useHandle(
          handle ->
              handle
                  .createUpdate(
                      "INSERT INTO characters (id, name, account, race, class, gender, appearance, created_on, delete_on, last_used_on) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                  .bind(0, character.id())
                  .bind(1, character.name())
                  .bind(2, character.account())
                  .bind(3, character.race().code)
                  .bind(4, character.clazz())
                  .bind(5, character.gender().code)
                  .bind(6, HexFormat.of().formatHex(character.appearance()))
                  .bind(7, character.createdOn())
                  .bind(8, character.deleteOn())
                  .bind(9, character.lastUsedOn())
                  .execute());
    } catch (StatementException e) {
      if (e.getCause() instanceof PSQLException psqlException
          && psqlException.getMessage().contains("duplicate key")) {
        throw new CharacterDuplicateException(character.name());
      }
      throw e;
    }
    logger.debug("Success");
  }

  @Override
  public List<Character> list(String account) {
    logger.debug("Fetching list of characters account='{}'", account);
    var result =
        db.withHandle(
            handle ->
                handle
                    .createQuery(
                        "SELECT id, name, account, race, class, gender, appearance, created_on, delete_on, last_used_on FROM characters WHERE account = ? ORDER BY created_on")
                    .bind(0, account)
                    .mapTo(Character.class)
                    .list());
    logger.debug("Characters are {}", result);
    return result;
  }

  @Override
  public void setDeleteOn(String id, Optional<LocalDateTime> deleteOn) {
    logger.debug("Updating deletion date id='{}' deleteOn={}", id, deleteOn);
    db.useHandle(
        handle ->
            handle
                .createUpdate("UPDATE characters SET delete_on = ? WHERE id = ?")
                .bind(1, id)
                .bind(0, deleteOn)
                .execute());
    logger.debug("Success");
  }

  @Override
  public void delete(String id) {
    logger.debug("Deleting character id='{}'", id);
    db.useHandle(
        handle -> handle.createUpdate("DELETE FROM characters WHERE id = ?").bind(0, id).execute());
    logger.debug("Success");
  }

  @Override
  public int deleteNow() {
    logger.debug("Deleting characters queued for deletion");
    var total =
        db.withHandle(
            handle ->
                handle.createUpdate("DELETE FROM characters WHERE delete_on < NOW()").execute());
    logger.debug("Cleaned up {} deleted character(s)", total);
    return total;
  }
}
