package com.github.goph3rx.world;

import java.util.List;

/** Adapter for the database that holds world information. */
public interface IWorldDatabase {
  /** Fetch the list of all worlds. */
  List<World> list();
}
