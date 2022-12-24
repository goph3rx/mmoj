package com.github.goph3rx.auth;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class AuthClientManagerTest extends MockitoTest {
  @Mock private AuthClient client;
  private AuthClientManager manager;

  @Before
  public void setUp() {
    manager = new AuthClientManager();
  }

  @Test
  public void add() {
    // Given
    when(client.getUsername()).thenReturn("hello");
    manager.add(client);

    // When
    var result = manager.find("hello");

    // Then
    assertTrue(result.isPresent());
    assertSame(result.get(), client);
  }

  @Test
  public void remove() {
    // Given
    manager.add(client);
    manager.remove(client);

    // When
    var result = manager.find("hello");

    // Then
    assertTrue(result.isEmpty());
  }
}
