package com.github.goph3rx.game;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for working with object identifiers. */
public class GameObjectIdService implements IGameObjectIdService {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(GameObjectIdService.class);

  /**
   * Next object identifier to be returned. We start with one as zero is used to indicate the
   * absence of an object.
   */
  private int nextObjectId = 1;
  /** Map for translating internal identifiers to object identifiers. */
  private final HashMap<String, Integer> idToObjectId = new HashMap<>();
  /** Map for translating object identifiers to internal identifiers. */
  private final HashMap<Integer, String> objectIdToId = new HashMap<>();
  /** Lock for synchronizing access. */
  private final ReentrantLock lock = new ReentrantLock();

  @Override
  public int generate(String id) {
    logger.debug("Generating object identifier id='{}'", id);
    lock.lock();
    int objectId;
    try {
      if (idToObjectId.containsKey(id)) {
        // Object identifier was allocated already
        objectId = idToObjectId.get(id);
      } else {
        // Create a new object identifier
        objectIdToId.put(nextObjectId, id);
        idToObjectId.put(id, nextObjectId);
        objectId = nextObjectId++;
      }
    } finally {
      lock.unlock();
    }
    logger.debug("Object identifier is {}", objectId);
    return objectId;
  }

  @Override
  public Optional<String> find(int objectId) {
    logger.debug("Looking for object objectId='{}'", objectId);
    lock.lock();
    try {
      var result = Optional.ofNullable(objectIdToId.get(objectId));
      logger.debug("Result is {}", result);
      return result;
    } finally {
      lock.unlock();
    }
  }
}
