package com.github.goph3rx.transfer;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/** Module that binds all transfer services. */
@Module
public interface TransferModule {
  @Binds
  @Singleton
  ITransferDatabase bindDatabase(TransferDatabase impl);

  @Binds
  @Singleton
  ITransferService bindService(TransferService impl);

  @Provides
  @Named("transfer.db")
  static String provideJdbcUrl() {
    return System.getProperty(
        "transfer.db", "jdbc:postgresql://127.0.0.1/transfers?user=transfers&password=changeme");
  }
}
