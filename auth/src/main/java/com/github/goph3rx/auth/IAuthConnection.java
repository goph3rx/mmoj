package com.github.goph3rx.auth;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Optional;

/** Connection to the auth server. */
public interface IAuthConnection {
  /** Get the remote address of the connection. */
  SocketAddress getRemoteAddress();

  /**
   * Send a message.
   *
   * @param message Message.
   */
  void send(Object message) throws IOException;

  /**
   * Receive a message.
   *
   * @return Message from the connection or empty optional if connection was closed.
   */
  Optional<Object> receive() throws IOException;

  /** Close the connection. */
  void close();
}
