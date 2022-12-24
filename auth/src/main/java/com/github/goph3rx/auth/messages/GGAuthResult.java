package com.github.goph3rx.auth.messages;

/** Result of GG auth. */
public enum GGAuthResult {
  SKIP(0x0b);

  /** Integer representation. */
  public final int code;

  GGAuthResult(int code) {
    this.code = code;
  }
}
