package com.github.goph3rx.auth;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/** Manager for clients connected to the server. */
public class AuthClientManager implements IAuthClientManager {
  /** List of clients connected to the server. */
  private final LinkedList<AuthClient> clients = new LinkedList<>();
  /** Lock for synchronizing access. */
  private final ReentrantLock lock = new ReentrantLock();

  @Override
  public void add(AuthClient client) {
    lock.lock();
    try {
      clients.add(client);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<AuthClient> find(String username) {
    lock.lock();
    try {
      return clients.stream().filter(client -> client.getUsername().equals(username)).findFirst();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void remove(AuthClient client) {
    lock.lock();
    try {
      clients.remove(client);
    } finally {
      lock.unlock();
    }
  }
}
