package com.github.goph3rx.character;

/** Character creation failed due to invalid name. */
public class CharacterNameException extends Exception {
  /**
   * Create a new exception.
   *
   * @param message Message.
   */
  public CharacterNameException(String message) {
    super(message);
  }
}
