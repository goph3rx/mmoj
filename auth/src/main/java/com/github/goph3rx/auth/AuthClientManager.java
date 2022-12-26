package com.github.goph3rx.auth;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Manager for clients connected to the server. */
public class AuthClientManager implements IAuthClientManager {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(AuthClientManager.class);

  /** List of clients connected to the server. */
  private final LinkedList<AuthClient> clients = new LinkedList<>();
  /** Lock for synchronizing access. */
  private final ReentrantLock lock = new ReentrantLock();

  @Override
  public void add(AuthClient client) {
    logger.debug("Adding client {}", client);
    lock.lock();
    try {
      clients.add(client);
    } finally {
      lock.unlock();
    }
    logger.debug("Success");
  }

  @Override
  public Optional<AuthClient> find(String username) {
    logger.debug("Finding clients username='{}'", username);
    lock.lock();
    try {
      var result =
          clients.stream().filter(client -> client.getUsername().equals(username)).findFirst();
      logger.debug("Result is {}", result);
      return result;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void remove(AuthClient client) {
    logger.debug("Removing client {}", client);
    lock.lock();
    try {
      clients.remove(client);
    } finally {
      lock.unlock();
    }
    logger.debug("Success");
  }
}
