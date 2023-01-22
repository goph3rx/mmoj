package com.github.goph3rx.character;

import java.util.Arrays;

/** Character races. */
public enum CharacterRace {
  HUMAN(0),
  ELF(1),
  DARK_ELF(2),
  ORC(3),
  DWARF(4),
  KAMAEL(5);

  /** All available enum values. */
  private static final CharacterRace[] VALUES = CharacterRace.values();

  /**
   * Initialize the enum from the code.
   *
   * @param code Code.
   * @return Race with this code or HUMAN in case of invalid input.
   */
  public static CharacterRace valueOf(int code) {
    return Arrays.stream(VALUES)
        .filter(v -> v.code == code)
        .findFirst()
        .orElse(CharacterRace.HUMAN);
  }

  /** Integer representation. */
  public final int code;

  CharacterRace(int code) {
    this.code = code;
  }
}
