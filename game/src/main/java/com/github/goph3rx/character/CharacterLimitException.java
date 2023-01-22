package com.github.goph3rx.character;

/** Character creation failed due to exceeding the limit on the account. */
public class CharacterLimitException extends Exception {
  /**
   * Create a new exception.
   *
   * @param message Message.
   */
  public CharacterLimitException(String message) {
    super(message);
  }
}
