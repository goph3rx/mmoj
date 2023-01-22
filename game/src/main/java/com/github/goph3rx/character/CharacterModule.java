package com.github.goph3rx.character;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/** Module that binds all character services. */
@Module
public interface CharacterModule {
  @Binds
  @Singleton
  ICharacterService bindService(CharacterService impl);

  @Binds
  @Singleton
  ICharacterDatabase bindDatabase(CharacterDatabase impl);

  @Provides
  @Named("character.namePattern")
  static String provideNamePattern() {
    return System.getProperty("character.namePattern", "^[A-Za-z0-9]{3,16}$");
  }

  @Provides
  @Named("character.deleteDays")
  static int provideDeleteDays() {
    return Integer.parseInt(System.getProperty("character.deleteDays", "7"));
  }

  @Provides
  @Named("character.db")
  static String provideJdbcUrl() {
    return System.getProperty(
        "character.db", "jdbc:postgresql://127.0.0.1/characters?user=characters&password=changeme");
  }
}
