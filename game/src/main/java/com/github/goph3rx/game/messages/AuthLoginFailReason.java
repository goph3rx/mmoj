package com.github.goph3rx.game.messages;

/** Reason for the login failure. */
public enum AuthLoginFailReason {
  NO_TEXT(0),
  SYSTEM_ERROR_LOGIN_LATER(1),
  PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT(2),
  PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2(3),
  ACCESS_FAILED_TRY_LATER(4),
  INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT(5),
  ACCESS_FAILED_TRY_LATER2(6),
  ACCOUNT_ALREADY_IN_USE(7);

  /** Integer representation. */
  public final int code;

  AuthLoginFailReason(int code) {
    this.code = code;
  }
}
