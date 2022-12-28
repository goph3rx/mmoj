package com.github.goph3rx;

import ch.qos.logback.classic.Level;
import com.github.goph3rx.auth.AuthServer;
import com.github.goph3rx.auth.DaggerAuthComponent;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** App that runs the auth part of the system. */
public class AuthApp {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(AuthApp.class);

  /**
   * Start the auth app.
   *
   * @param args CLI arguments.
   */
  public static void main(String[] args) {
    // Load configuration
    var propertiesFileName = "auth.properties";
    try (var propertiesFile = new FileInputStream(propertiesFileName)) {
      System.getProperties().load(propertiesFile);
    } catch (IOException e) {
      logger.warn("{} not found, assuming default configuration", propertiesFileName);
    }

    // Configure logging
    try {
      var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      logger.setLevel(Level.toLevel(System.getProperty("auth.log", "DEBUG")));
    } catch (Exception e) {
      logger.warn("Failed to configure logging", e);
    }

    // Create the services
    var component = DaggerAuthComponent.create();
    var server = new AuthServer();
    component.injectServer(server);

    // Start background tasks
    component.transfers().start();
    component.worlds().start();

    // Start the server
    try {
      server.run(component);
    } catch (Exception e) {
      logger.error("Failed to start server", e);
    }
  }
}
