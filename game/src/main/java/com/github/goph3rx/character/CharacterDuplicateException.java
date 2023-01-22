package com.github.goph3rx.character;

/** Character creation failed due to duplicate name. */
public class CharacterDuplicateException extends Exception {
  /**
   * Create a new exception.
   *
   * @param name Character name.
   */
  public CharacterDuplicateException(String name) {
    super("New character name already exists name='%s'".formatted(name));
  }
}
