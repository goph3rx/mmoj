package com.github.goph3rx.character;

import java.util.List;

/** Service for managing characters. */
public interface ICharacterService {
  /** Get the list of templates for character creation. */
  List<CharacterTemplate> templates();

  /**
   * Create a new character.
   *
   * @param account Account name.
   * @param name Name.
   * @param race Race.
   * @param gender Gender.
   * @param clazz Base class.
   * @param appearance Appearance.
   * @throws CharacterCreateException Invalid race/class/gender supplied.
   * @throws CharacterNameException Invalid name supplied.
   * @throws CharacterLimitException Limit reached for number of characters on this account.
   * @throws CharacterDuplicateException Character with this name already exists.
   */
  void create(
      String account,
      String name,
      CharacterRace race,
      CharacterGender gender,
      int clazz,
      byte[] appearance)
      throws CharacterCreateException, CharacterNameException, CharacterLimitException,
          CharacterDuplicateException;

  /**
   * List the characters on this account.
   *
   * @param account Account name.
   * @return List of characters.
   */
  List<Character> list(String account);

  /**
   * Remove the character.
   *
   * @param account Account name.
   * @param index Index of the character in the list.
   */
  void remove(String account, int index);

  /**
   * Restore the character queued for removal earlier.
   *
   * @param account Account name.
   * @param index Index of the character in the list.
   */
  void restore(String account, int index);

  /** Initialize this service and start any background tasks. */
  void start();
}
