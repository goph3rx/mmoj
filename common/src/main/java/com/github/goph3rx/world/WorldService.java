package com.github.goph3rx.world;

import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing registered worlds. */
public class WorldService implements IWorldService {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(WorldService.class);

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
}
