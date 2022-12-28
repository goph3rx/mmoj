package com.github.goph3rx;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.goph3rx.world.IWorldDatabase;
import com.github.goph3rx.world.World;
import com.github.goph3rx.world.WorldService;
import java.net.Inet4Address;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class WorldServiceTest extends MockitoTest {
  private static final World WORLD =
      new World(1, Inet4Address.getLoopbackAddress(), 7777, 0, 1000, true);

  @Mock private IWorldDatabase database;
  @InjectMocks private WorldService service;

  @Captor private ArgumentCaptor<World> world;

  @Test
  public void list() {
    // Given
    var expected = List.of(WORLD);
    when(database.list()).thenReturn(expected);

    // When
    var actual = service.list();

    // Then
    assertSame(expected, actual);
  }

  @Test
  public void save() {
    // When
    service.save(WORLD);

    // Then
    verify(database).save(world.capture());
    assertSame(WORLD, world.getValue());
  }
}
