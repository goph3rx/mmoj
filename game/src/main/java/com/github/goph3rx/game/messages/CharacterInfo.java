package com.github.goph3rx.game.messages;

import com.github.goph3rx.character.Character;

/**
 * Complete information about the character.
 *
 * @param objectId Object identifier.
 * @param general General character information.
 */
public record CharacterInfo(int objectId, Character general) {}
