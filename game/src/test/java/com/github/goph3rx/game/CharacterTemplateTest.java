package com.github.goph3rx.game;

import static org.junit.Assert.*;

import com.github.goph3rx.character.CharacterGender;
import com.github.goph3rx.character.CharacterRace;
import com.github.goph3rx.character.CharacterTemplate;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class CharacterTemplateTest {
  @Test
  public void load() throws IOException {
    // Given
    var source =
        """
          // Fighter
          [{
            "race": "HUMAN",
            "class": 0
          },
          // Female soldier
          {
            "race": "KAMAEL",
            "class": 124,
            "gender": "FEMALE"
          }]
       """;

    // When
    List<CharacterTemplate> templates;
    try (var reader = new StringReader(source)) {
      templates = CharacterTemplate.load(reader);
    }

    // Then
    assertEquals(2, templates.size());
    var template = templates.get(0);
    assertEquals(CharacterRace.HUMAN, template.race());
    assertEquals(0, template.clazz());
    assertTrue(template.gender().isEmpty());
    template = templates.get(1);
    assertEquals(CharacterRace.KAMAEL, template.race());
    assertEquals(124, template.clazz());
    assertEquals(Optional.of(CharacterGender.FEMALE), template.gender());
  }
}
