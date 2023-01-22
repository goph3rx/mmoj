package com.github.goph3rx.game;

import static org.junit.Assert.assertEquals;

import com.github.goph3rx.character.CharacterRace;
import org.junit.Test;

public class CharacterRaceTest {
  @Test
  public void valueOfValid() {
    // Given
    var code = 3;

    // When
    var race = CharacterRace.valueOf(code);

    // Then
    assertEquals(CharacterRace.ORC, race);
  }

  @Test
  public void valueOfInvalid() {
    // Given
    var code = -1;

    // When
    var race = CharacterRace.valueOf(code);

    // Then
    assertEquals(CharacterRace.HUMAN, race);
  }
}
