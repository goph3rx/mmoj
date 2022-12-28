package com.github.goph3rx.world;

import java.util.List;

/** Service for managing registered worlds. */
public interface IWorldService {
  /** Fetch the list of all worlds. */
  List<World> list();

  /**
   * Save the status of the game world.
   *
   * @param world World status.
   */
  void save(World world);

  /** Start the background tasks for this service. */
  void start();
}
