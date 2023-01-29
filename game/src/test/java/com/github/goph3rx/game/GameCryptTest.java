package com.github.goph3rx.game;

import static org.junit.Assert.assertEquals;

import java.util.HexFormat;
import org.junit.Test;

public class GameCryptTest {
  @Test
  public void encrypt() {
    // Given
    var buffer = HexFormat.of().parseHex("09000000000700000000");
    var cipher = new GameCrypt(HexFormat.of().parseHex("48f2b96d78a4e86e"));

    // When
    cipher.encrypt(buffer, 0, buffer.length);

    // Then
    assertEquals("41b30a671fbc543af2d5", HexFormat.of().formatHex(buffer));
  }

  @Test
  public void decrypt() {
    // Given
    var buffer =
        HexFormat.of()
            .parseHex(
                "709c1f71e19d0a77bf980bc071dcde999975dc80f63ae9ea6282c3c3620e3fa8f36be8e3737ceb");
    var cipher = new GameCrypt(HexFormat.of().parseHex("5b98830b900f9709"));

    // When
    cipher.decrypt(buffer, 0, buffer.length);

    // Then
    assertEquals(
        "2b74006500730074000000ca10c133d05b742a57e6c3440a40c7d2010000000000000000000000",
        HexFormat.of().formatHex(buffer));
  }
}
