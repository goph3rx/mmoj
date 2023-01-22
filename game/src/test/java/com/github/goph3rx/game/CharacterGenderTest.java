package com.github.goph3rx.game;

import static org.junit.Assert.assertEquals;

import com.github.goph3rx.character.CharacterGender;
import org.junit.Test;

public class CharacterGenderTest {
  @Test
  public void valueOfValid() {
    // Given
    var code = 1;

    // When
    var gender = CharacterGender.valueOf(code);

    // Then
    assertEquals(CharacterGender.FEMALE, gender);
  }

  @Test
  public void valueOfInvalid() {
    // Given
    var code = -1;

    // When
    var gender = CharacterGender.valueOf(code);

    // Then
    assertEquals(CharacterGender.MALE, gender);
  }
}
