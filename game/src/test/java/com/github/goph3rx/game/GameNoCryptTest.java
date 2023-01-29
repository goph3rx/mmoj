package com.github.goph3rx.game;

import static org.junit.Assert.assertEquals;

import java.util.HexFormat;
import org.junit.Test;

public class GameNoCryptTest {
  @Test
  public void encrypt() {
    // Given
    var buffer = HexFormat.of().parseHex("09000000000700000000");
    var cipher = new GameNoCrypt();

    // When
    cipher.encrypt(buffer, 0, buffer.length);

    // Then
    assertEquals("09000000000700000000", HexFormat.of().formatHex(buffer));
  }

  @Test
  public void decrypt() {
    // Given
    var buffer = HexFormat.of().parseHex("2b74006500730074000000");
    var cipher = new GameNoCrypt();

    // When
    cipher.decrypt(buffer, 0, buffer.length);

    // Then
    assertEquals("2b74006500730074000000", HexFormat.of().formatHex(buffer));
  }
}
