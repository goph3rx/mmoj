package com.github.goph3rx.auth;

import static org.junit.Assert.assertEquals;

import java.util.HexFormat;
import javax.crypto.ShortBufferException;
import org.junit.Test;

public class AuthBlowfishTest {
  @Test
  public void encrypt() throws ShortBufferException {
    // Given
    var buffer = HexFormat.of().parseHex("0102030405060708090a0b0c0d0e0f10");
    var cipher = new AuthBlowfish();

    // When
    cipher.encrypt(buffer, 0, buffer.length);

    // Then
    assertEquals("569a83df25c8816a99368a84726f4fd7", HexFormat.of().formatHex(buffer));
  }

  @Test
  public void decrypt() throws ShortBufferException {
    // Given
    var buffer = HexFormat.of().parseHex("0102030405060708090a0b0c0d0e0f10");
    var cipher = new AuthBlowfish();

    // When
    cipher.decrypt(buffer, 0, buffer.length);

    // Then
    assertEquals("741fe2984851ecb0544f3ea3f29bcd35", HexFormat.of().formatHex(buffer));
  }
}
