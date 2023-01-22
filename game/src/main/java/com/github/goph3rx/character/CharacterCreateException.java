package com.github.goph3rx.character;

/** Character creation failed due to invalid inputs. */
public class CharacterCreateException extends Exception {
  /**
   * Create a new exception.
   *
   * @param message Message.
   */
  public CharacterCreateException(String message) {
    super(message);
  }
}
