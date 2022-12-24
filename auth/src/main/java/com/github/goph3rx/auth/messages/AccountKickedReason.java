package com.github.goph3rx.auth.messages;

/** Reason for the account being banned. */
public enum AccountKickedReason {
  DATA_STEALER(0x01),
  GENERIC_VIOLATION(0x08),
  SEVEN_DAYS_SUSPENDED(0x10),
  PERMANENTLY_BANNED(0x20);

  /** Integer representation. */
  public final int code;

  AccountKickedReason(int code) {
    this.code = code;
  }
}
