package com.github.goph3rx.world;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adapter for the database that holds world information. */
public class WorldDatabase implements IWorldDatabase {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(WorldDatabase.class);

  /** Database. */
  private final Jdbi db;

  @Inject
  public WorldDatabase(@Named("world.db") String jdbcUrl) {
    var config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setPoolName("worlds");
    db = Jdbi.create(new HikariDataSource(config));
    db.registerRowMapper(ConstructorMapper.factory(World.class));
  }

  @Override
  public List<World> list() {
    logger.debug("Fetching list of worlds");
    var result =
        db.withHandle(
            handle ->
                handle
                    .createQuery(
                        "SELECT id, ip, port, current_players, maximum_players, is_online FROM worlds ORDER BY id")
                    .mapTo(World.class)
                    .list());
    logger.debug("Worlds are {}", result);
    return result;
  }

  @Override
  public void save(World world) {
    logger.debug("Saving world {}", world);
    db.useHandle(
        handle ->
            handle
                .createUpdate(
                    "INSERT INTO worlds (id, ip, port, current_players, maximum_players, is_online, last_updated) VALUES (?, ?::inet, ?, ?, ?, ?, NOW()) ON CONFLICT (id) DO UPDATE SET ip = EXCLUDED.ip, port = EXCLUDED.port, current_players = EXCLUDED.current_players, maximum_players = EXCLUDED.maximum_players, is_online = EXCLUDED.is_online, last_updated = EXCLUDED.last_updated")
                .bind(0, world.id())
                .bind(1, world.ip())
                .bind(2, world.port())
                .bind(3, world.currentPlayers())
                .bind(4, world.maximumPlayers())
                .bind(5, world.isOnline())
                .execute());
    logger.debug("Success");
  }

  @Override
  public int updateOffline() {
    logger.debug("Setting worlds as offline");
    var total =
        db.withHandle(
            handle ->
                handle
                    .createUpdate(
                        "UPDATE worlds SET is_online = false WHERE last_updated < NOW() - INTERVAL '10 seconds' AND is_online = true")
                    .execute());
    logger.debug("{} world(s) went offline", total);
    return total;
  }
}
