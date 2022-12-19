package com.github.goph3rx;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.github.goph3rx.world.IWorldDatabase;
import com.github.goph3rx.world.World;
import com.github.goph3rx.world.WorldService;
import java.net.Inet4Address;
import java.util.List;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class WorldServiceTest extends MockitoTest {

  @Mock private IWorldDatabase database;
  @InjectMocks private WorldService service;

  @Test
  public void generate() {
    // Given
    var expected = List.of(new World(1, Inet4Address.getLoopbackAddress(), 7777, 0, 1000, true));
    when(database.list()).thenReturn(expected);

    // When
    var actual = service.list();

    // Then
    assertSame(expected, actual);
  }
}
