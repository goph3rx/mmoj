package com.github.goph3rx.transfer;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/** Module that binds all transfer services. */
public class TransferModule extends AbstractModule {
  @Override
  protected void configure() {
    // Configuration
    bind(String.class)
        .annotatedWith(Names.named("transfer.db"))
        .toInstance(
            System.getProperty(
                "transfer.db",
                "jdbc:postgresql://127.0.0.1/transfers?user=transfers&password=changeme"));

    // Services
    bind(ITransferDatabase.class).to(TransferDatabase.class).in(Scopes.SINGLETON);
    bind(ITransferService.class).to(TransferService.class).in(Scopes.SINGLETON);
  }
}
