package com.github.goph3rx.auth;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Server for clients to connect to for authentication. */
public class AuthServer {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(AuthServer.class);
  /** Secure random number generation. */
  private static final SecureRandom random = new SecureRandom();
  /** Encoding and decoding of messages. */
  private static final AuthCodec codec = new AuthCodec();

  /** Port. */
  @Named("auth.port")
  @Inject
  public int port;
  /** Maximum allowed number of connections waiting to be accepted. */
  @Named("auth.backlog")
  @Inject
  public int backlog;

  /**
   * Run the server.
   *
   * @param component Component for dependency injection.
   * @throws IOException Server cannot be started.
   */
  public void run(AuthComponent component) throws IOException {
    // Create the server
    try (var socket = new ServerSocket(port, backlog)) {
      logger.info("Starting server on {}", socket.getLocalSocketAddress());

      // Start accepting clients
      try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
        while (true) {
          try {
            // Create a connection
            var scrambleKey = random.nextInt();
            var crypt = new AuthBlowfish();
            var connection = new AuthConnection(socket.accept(), codec, crypt, scrambleKey);

            // Create a client and start processing it
            var client = new AuthClient(connection);
            component.injectClient(client);
            pool.submit(client);
          } catch (Exception e) {
            logger.error("Failed to accept connection", e);
          }
        }
      }
    }
  }
}
