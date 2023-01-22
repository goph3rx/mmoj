package com.github.goph3rx.character;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Adapter for the database that holds general character information. */
public interface ICharacterDatabase {
  /**
   * Create a new character.
   *
   * @param character Character information.
   * @throws CharacterDuplicateException Character with this name already exists.
   */
  void create(Character character) throws CharacterDuplicateException;

  /**
   * List the characters on this account.
   *
   * @param account Account name.
   * @return List of characters.
   */
  List<Character> list(String account);

  /**
   * Update the deletion date for the character.
   *
   * @param id Character identifier.
   * @param deleteOn Deletion date. Empty value means that this character is no longer queued for
   *     deletion.
   */
  void setDeleteOn(String id, Optional<LocalDateTime> deleteOn);

  /**
   * Delete the character.
   *
   * @param id Character identifier.
   */
  void delete(String id);

  /**
   * Delete the characters queued for deletion.
   *
   * @return Total number of characters deleted.
   */
  int deleteNow();
}
