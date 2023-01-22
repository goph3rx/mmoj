package com.github.goph3rx.game;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.goph3rx.character.*;
import com.github.goph3rx.character.Character;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

public class CharacterServiceTest extends MockitoTest {
  private static final List<CharacterTemplate> TEMPLATES =
      List.of(
          new CharacterTemplate(CharacterRace.HUMAN, 0, Optional.empty()),
          new CharacterTemplate(CharacterRace.KAMAEL, 124, Optional.of(CharacterGender.FEMALE)));
  private static final Character CHARACTER =
      new Character(
          "char:123",
          "Character",
          "hello",
          CharacterRace.HUMAN,
          0,
          CharacterGender.FEMALE,
          new byte[0],
          LocalDateTime.now(),
          Optional.empty(),
          Optional.empty());

  @Mock private ICharacterDatabase database;
  private CharacterService service;

  @Captor private ArgumentCaptor<Character> character;
  @Captor private ArgumentCaptor<Optional<LocalDateTime>> deleteOn;

  @Before
  public void setUp() {
    service = new CharacterService(CharacterModule.provideNamePattern());
    service.deleteDays = CharacterModule.provideDeleteDays();
    service.database = database;
    service.setTemplates(TEMPLATES);
  }

  @Test
  public void templates() {
    // When
    var templates = service.templates();

    // Then
    assertSame(TEMPLATES, templates);
  }

  @Test(expected = CharacterCreateException.class)
  public void createInvalidClass()
      throws CharacterDuplicateException, CharacterCreateException, CharacterLimitException,
          CharacterNameException {
    // When/Then
    service.create(
        "hello", "Character", CharacterRace.HUMAN, CharacterGender.FEMALE, -1, new byte[0]);
  }

  @Test(expected = CharacterCreateException.class)
  public void createInvalidGender()
      throws CharacterDuplicateException, CharacterCreateException, CharacterLimitException,
          CharacterNameException {
    // When/Then
    service.create(
        "hello", "Character", CharacterRace.KAMAEL, CharacterGender.MALE, 124, new byte[0]);
  }

  @Test(expected = CharacterNameException.class)
  public void createInvalidName()
      throws CharacterDuplicateException, CharacterCreateException, CharacterLimitException,
          CharacterNameException {
    // When/Then
    service.create("hello", "", CharacterRace.HUMAN, CharacterGender.MALE, 0, new byte[0]);
  }

  @Test(expected = CharacterLimitException.class)
  public void createExceededLimit()
      throws CharacterDuplicateException, CharacterCreateException, CharacterLimitException,
          CharacterNameException {
    // Given
    when(database.list("hello")).thenReturn(Collections.nCopies(7, CHARACTER));

    // When/Then
    service.create("hello", "Character", CharacterRace.HUMAN, CharacterGender.MALE, 0, new byte[0]);
  }

  @Test
  public void createSuccess()
      throws CharacterDuplicateException, CharacterCreateException, CharacterLimitException,
          CharacterNameException {
    // Given
    when(database.list("hello")).thenReturn(List.of());

    // When
    service.create("hello", "Character", CharacterRace.HUMAN, CharacterGender.MALE, 0, new byte[0]);

    // Then
    verify(database).create(character.capture());
    var myCharacter = character.getValue();
    assertTrue(myCharacter.id().startsWith(Character.PREFIX));
    assertEquals("Character", myCharacter.name());
    assertEquals("hello", myCharacter.account());
    assertEquals(CharacterRace.HUMAN, myCharacter.race());
    assertEquals(0, myCharacter.clazz());
    assertEquals(0, myCharacter.appearance().length);
    assertTrue(myCharacter.deleteOn().isEmpty());
    assertTrue(myCharacter.lastUsedOn().isEmpty());
  }

  @Test(expected = CharacterDuplicateException.class)
  public void createDuplicateName()
      throws CharacterDuplicateException, CharacterCreateException, CharacterLimitException,
          CharacterNameException {
    // Given
    when(database.list("hello")).thenReturn(List.of());
    doThrow(new CharacterDuplicateException("Character"))
        .when(database)
        .create(any(Character.class));

    // When/Then
    service.create("hello", "Character", CharacterRace.HUMAN, CharacterGender.MALE, 0, new byte[0]);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeInvalidIndex() {
    // Given
    when(database.list("hello")).thenReturn(List.of());

    // When/Then
    service.remove("hello", -1);
  }

  @Test
  public void removeLaterSuccess() {
    // Given
    when(database.list("hello")).thenReturn(List.of(CHARACTER));

    // When
    service.remove("hello", 0);

    // Then
    verify(database).setDeleteOn(eq(CHARACTER.id()), deleteOn.capture());
    assertTrue(deleteOn.getValue().isPresent());
    verify(database, never()).delete(CHARACTER.id());
  }

  @Test
  public void removeNowSuccess() {
    // Given
    service.deleteDays = 0;
    when(database.list("hello")).thenReturn(List.of(CHARACTER));

    // When
    service.remove("hello", 0);

    // Then
    verify(database).delete(CHARACTER.id());
    verify(database, never()).setDeleteOn(eq(CHARACTER.id()), any());
  }

  @Test
  public void restore() {
    // Given
    when(database.list("hello")).thenReturn(List.of(CHARACTER));

    // When
    service.restore("hello", 0);

    // Then
    verify(database).setDeleteOn(eq(CHARACTER.id()), deleteOn.capture());
    assertTrue(deleteOn.getValue().isEmpty());
  }
}
