package com.github.goph3rx.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GameObjectIdServiceTest {
  private GameObjectIdService service;

  @Before
  public void setUp() {
    service = new GameObjectIdService();
  }

  @Test
  public void generateSame() {
    // When
    var objectId1 = service.generate("char:123");
    var objectId2 = service.generate("char:123");

    // Then
    assertEquals(1, objectId1);
    assertEquals(1, objectId2);
  }

  @Test
  public void generateDifferent() {
    // When
    var objectId1 = service.generate("char:123");
    var objectId2 = service.generate("char:456");

    // Then
    assertEquals(1, objectId1);
    assertEquals(2, objectId2);
  }

  @Test
  public void findMissing() {
    // When
    var id = service.find(1);

    // Then
    assertTrue(id.isEmpty());
  }

  @Test
  public void findSuccess() {
    // Given
    var objectId = service.generate("char:123");

    // When
    var id = service.find(objectId);

    // Then
    assertTrue(id.isPresent());
    assertEquals("char:123", id.get());
  }
}
