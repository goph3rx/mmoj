package com.github.goph3rx.game.messages;

/**
 * Request to delete a character.
 *
 * @param index Index of the character.
 */
public record ClientCharDelete(int index) {}
