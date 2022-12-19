package com.github.goph3rx.world;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/** Module that binds all world listing services. */
@Module
public interface WorldModule {
  @Binds
  @Singleton
  IWorldDatabase bindDatabase(WorldDatabase impl);

  @Binds
  @Singleton
  IWorldService bindService(WorldService impl);

  @Provides
  @Named("world.db")
  static String provideJdbcUrl() {
    return System.getProperty(
        "world.db", "jdbc:postgresql://127.0.0.1/worlds?user=worlds&password=changeme");
  }
}
