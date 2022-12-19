package com.github.goph3rx.world;

import java.util.List;

/** Service for managing registered worlds. */
public interface IWorldService {
  /** Fetch the list of all worlds. */
  List<World> list();
}
