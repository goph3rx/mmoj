package com.github.goph3rx.game.messages;

import com.github.goph3rx.character.CharacterTemplate;
import java.util.List;

/**
 * Response to list the available character templates.
 *
 * @param templates Templates.
 */
public record ServerCharTemplates(List<CharacterTemplate> templates) {}
