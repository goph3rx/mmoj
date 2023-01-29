package com.github.goph3rx.game.messages;

/** Reason for the character creation failure. */
public enum CharCreateFailReason {
  CREATION_FAILED(0),
  TOO_MANY_CHARACTERS(1),
  NAME_ALREADY_EXISTS(2),
  SIXTEEN_ENG_CHARS(3);

  /** Integer representation. */
  public final int code;

  CharCreateFailReason(int code) {
    this.code = code;
  }
}
