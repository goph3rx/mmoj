package com.github.goph3rx.character;

import java.util.Arrays;

/** Gender for the character. */
public enum CharacterGender {
  MALE(0),
  FEMALE(1);

  /** All available enum values. */
  private static final CharacterGender[] VALUES = CharacterGender.values();

  /**
   * Initialize the enum from the code.
   *
   * @param code Code.
   * @return Gender with this code or MALE in case of invalid input.
   */
  public static CharacterGender valueOf(int code) {
    return Arrays.stream(VALUES)
        .filter(v -> v.code == code)
        .findFirst()
        .orElse(CharacterGender.MALE);
  }

  /** Integer representation. */
  public final int code;

  CharacterGender(int code) {
    this.code = code;
  }
}
