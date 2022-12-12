package com.github.goph3rx.account;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/** Module that binds all account services. */
public class AccountModule extends AbstractModule {
  @Override
  protected void configure() {
    // Configuration
    bind(String.class)
        .annotatedWith(Names.named("account.db"))
        .toInstance(
            System.getProperty(
                "account.db",
                "jdbc:postgresql://127.0.0.1/accounts?user=accounts&password=changeme"));

    // Services
    bind(IAccountDatabase.class).to(AccountDatabase.class).in(Scopes.SINGLETON);
    bind(IAccountService.class).to(AccountService.class).in(Scopes.SINGLETON);
  }
}
