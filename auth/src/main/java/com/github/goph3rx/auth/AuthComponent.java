package com.github.goph3rx.auth;

import com.github.goph3rx.account.AccountModule;
import com.github.goph3rx.transfer.ITransferService;
import com.github.goph3rx.transfer.TransferModule;
import com.github.goph3rx.world.IWorldService;
import com.github.goph3rx.world.WorldModule;
import dagger.Component;
import javax.inject.Singleton;

/** Main component for the auth server. */
@Singleton
@Component(
    modules = {AuthModule.class, AccountModule.class, TransferModule.class, WorldModule.class})
public interface AuthComponent {
  /**
   * Inject dependencies into the client.
   *
   * @param client Client.
   */
  void injectClient(AuthClient client);

  /**
   * Inject dependencies into the server.
   *
   * @param server Server.
   */
  void injectServer(AuthServer server);

  /** Get the service for world transfers. */
  ITransferService transfers();

  /** Get the service for managing registered worlds. */
  IWorldService worlds();
}
