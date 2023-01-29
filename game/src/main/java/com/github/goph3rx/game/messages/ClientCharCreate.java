package com.github.goph3rx.game.messages;

import com.github.goph3rx.character.CharacterGender;
import com.github.goph3rx.character.CharacterRace;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Request to complete character creation.
 *
 * @param name Name.
 * @param race Race.
 * @param gender Gender.
 * @param clazz Base class.
 * @param appearance Appearance.
 */
public record ClientCharCreate(
    String name, CharacterRace race, CharacterGender gender, int clazz, byte[] appearance) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientCharCreate that = (ClientCharCreate) o;
    return clazz == that.clazz
        && name.equals(that.name)
        && race == that.race
        && gender == that.gender
        && Arrays.equals(appearance, that.appearance);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(name, race, gender, clazz);
    result = 31 * result + Arrays.hashCode(appearance);
    return result;
  }

  @Override
  public String toString() {
    return "ClientCharCreate["
        + "name='"
        + name
        + '\''
        + ", race="
        + race
        + ", gender="
        + gender
        + ", clazz="
        + clazz
        + ", appearance="
        + HexFormat.of().formatHex(appearance)
        + ']';
  }
}
