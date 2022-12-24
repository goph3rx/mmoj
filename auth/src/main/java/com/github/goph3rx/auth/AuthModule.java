package com.github.goph3rx.auth;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/** Module that binds all auth services. */
@Module
public interface AuthModule {
  @Provides
  @Singleton
  static IAuthCredentialKey provideCredentialKey() {
    return new AuthCredentialKey();
  }

  @Provides
  @Singleton
  static IAuthClientManager provideClientManager() {
    return new AuthClientManager();
  }

  @Provides
  @Named("auth.port")
  static int providePort() {
    return Integer.parseInt(System.getProperty("auth.port", "2106"));
  }

  @Provides
  @Named("auth.backlog")
  static int provideBacklog() {
    return Integer.parseInt(System.getProperty("auth.backlog", "10"));
  }
}
