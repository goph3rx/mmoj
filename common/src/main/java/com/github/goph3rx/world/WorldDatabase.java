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
}
