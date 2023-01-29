package com.github.goph3rx.game.messages;

/** Reason for the character deletion failure. */
public enum CharDeleteFailReason {
  DELETION_FAILED(1),
  YOU_MAY_NOT_DELETE_CLAN_MEMBER(2),
  CLAN_LEADERS_MAY_NOT_BE_DELETED(3);

  /** Integer representation. */
  public final int code;

  CharDeleteFailReason(int code) {
    this.code = code;
  }
}
