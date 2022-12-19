package com.github.goph3rx.account;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/** Module that binds all account services. */
@Module
public interface AccountModule {
  @Binds
  @Singleton
  IAccountDatabase bindDatabase(AccountDatabase impl);

  @Binds
  @Singleton
  IAccountService bindService(AccountService impl);

  @Provides
  @Named("account.db")
  static String provideJdbcUrl() {
    return System.getProperty(
        "account.db", "jdbc:postgresql://127.0.0.1/accounts?user=accounts&password=changeme");
  }
}
