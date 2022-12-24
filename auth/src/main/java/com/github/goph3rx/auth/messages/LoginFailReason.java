package com.github.goph3rx.auth.messages;

/** Reason for the login failure. */
public enum LoginFailReason {
  SYSTEM_ERROR(0x01),
  PASS_WRONG(0x02),
  USER_OR_PASS_WRONG(0x03),
  ACCESS_FAILED(0x04),
  ACCOUNT_IN_USE(0x07),
  SERVER_OVERLOADED(0x0f),
  SERVER_MAINTENANCE(0x10),
  TEMP_PASS_EXPIRED(0x11),
  DUAL_BOX(0x23);

  /** Integer representation. */
  public final int code;

  LoginFailReason(int code) {
    this.code = code;
  }
}
