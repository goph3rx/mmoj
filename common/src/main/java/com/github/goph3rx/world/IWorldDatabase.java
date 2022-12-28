package com.github.goph3rx.world;

import java.util.List;

/** Adapter for the database that holds world information. */
public interface IWorldDatabase {
  /** Fetch the list of all worlds. */
  List<World> list();

  /**
   * Save the status of the game world.
   *
   * @param world World status.
   */
  void save(World world);

  /**
   * Set worlds to offline in case of no recent updates.
   *
   * @return Total number of worlds marked offline.
   */
  int updateOffline();
}
