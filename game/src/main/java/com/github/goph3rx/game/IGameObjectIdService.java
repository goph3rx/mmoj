package com.github.goph3rx.game;

import java.util.Optional;

/** Service for working with object identifiers. */
public interface IGameObjectIdService {
  /**
   * Generate the object identifier for this object. Repeated calls with the same input will result
   * in the same object identifier.
   *
   * @param id Internal identifier.
   * @return Object identifier.
   */
  int generate(String id);

  /**
   * Find the internal identifier by object identifier.
   *
   * @param objectId Object identifier.
   * @return Internal identifier, if found.
   */
  Optional<String> find(int objectId);
}
