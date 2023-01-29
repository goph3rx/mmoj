package com.github.goph3rx.game.messages;

import java.util.List;

/**
 * Response to list the characters on the account.
 *
 * @param characters List of characters.
 */
public record ServerCharSelectInfo(List<CharacterInfo> characters) {}
