package com.github.goph3rx.game.messages;

/**
 * Request to restore the character queued for removal earlier.
 *
 * @param index Index of the character.
 */
public record ClientCharRestore(int index) {}
