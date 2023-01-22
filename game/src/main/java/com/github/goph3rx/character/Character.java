package com.github.goph3rx.character;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * General information about the character.
 *
 * @param id Unique identifier.
 * @param name Name.
 * @param account Account.
 * @param race Race.
 * @param clazz Base class.
 * @param gender Gender.
 * @param appearance Appearance.
 * @param createdOn Date and time of creation.
 * @param deleteOn Date and time of deletion (if character is queued for deletion).
 * @param lastUsedOn Date and time of last use (if character was ever used).
 */
public record Character(
    String id,
    String name,
    String account,
    CharacterRace race,
    int clazz,
    CharacterGender gender,
    byte[] appearance,
    LocalDateTime createdOn,
    Optional<LocalDateTime> deleteOn,
    Optional<LocalDateTime> lastUsedOn) {
  /** Prefix used in unique identifiers for characters. */
  public static final String PREFIX = "char:";

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Character character = (Character) o;
    return clazz == character.clazz
        && id.equals(character.id)
        && name.equals(character.name)
        && account.equals(character.account)
        && race == character.race
        && gender == character.gender
        && Arrays.equals(appearance, character.appearance)
        && createdOn.equals(character.createdOn)
        && deleteOn.equals(character.deleteOn)
        && lastUsedOn.equals(character.lastUsedOn);
  }

  @Override
  public int hashCode() {
    int result =
        Objects.hash(id, name, account, race, clazz, gender, createdOn, deleteOn, lastUsedOn);
    result = 31 * result + Arrays.hashCode(appearance);
    return result;
  }

  @Override
  public String toString() {
    return "Character["
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", account='"
        + account
        + '\''
        + ", race="
        + race
        + ", clazz="
        + clazz
        + ", gender="
        + gender
        + ", appearance="
        + HexFormat.of().formatHex(appearance)
        + ", createdOn="
        + createdOn
        + ", deleteOn="
        + deleteOn
        + ", lastUsedOn="
        + lastUsedOn
        + ']';
  }
}
