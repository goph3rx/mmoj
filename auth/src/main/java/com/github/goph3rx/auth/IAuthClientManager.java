package com.github.goph3rx.auth;

import java.util.Optional;

/** Manager for clients connected to the server. */
public interface IAuthClientManager {
  /**
   * Add the client to the list of connected ones.
   *
   * @param client Client.
   */
  void add(AuthClient client);

  /**
   * Find a connected client.
   *
   * @param username Account username.
   * @return Client, if found.
   */
  Optional<AuthClient> find(String username);

  /**
   * Remove the client from the list of connected ones.
   *
   * @param client Client.
   */
  void remove(AuthClient client);
}
