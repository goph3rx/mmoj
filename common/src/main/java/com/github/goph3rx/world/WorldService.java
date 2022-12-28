package com.github.goph3rx.world;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing registered worlds. */
public class WorldService implements IWorldService {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(WorldService.class);
  /** Executor for scheduled tasks originating from this class. */
  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());
  /** How often to clean up the expired tokens. */
  private static final int WORLD_UPDATE_SECONDS = 10;

  /** Adapter for the database. */
  @Inject public IWorldDatabase database;

  /** Create a new service. */
  @Inject
  public WorldService() {
    // Constructor is required for DI
  }

  @Override
  public List<World> list() {
    logger.debug("Fetching list of worlds");
    var result = database.list();
    logger.debug("Worlds are {}", result);
    return result;
  }

  @Override
  public void save(World world) {
    logger.debug("Saving world {}", world);
    database.save(world);
    logger.debug("Success");
  }

  @Override
  public void start() {
    executor.scheduleAtFixedRate(
        () -> {
          try {
            var total = database.updateOffline();
            if (total > 0) {
              logger.info("{} world(s) went offline", total);
            }
          } catch (Exception e) {
            logger.warn("Failed to update the worlds", e);
          }
        },
        0,
        WORLD_UPDATE_SECONDS,
        TimeUnit.SECONDS);
  }
}
