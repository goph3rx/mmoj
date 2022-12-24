package com.github.goph3rx.auth;

import static org.junit.Assert.assertEquals;

import java.util.HexFormat;
import org.junit.Test;

public class AuthCryptUtilTest {
  @Test
  public void scrambleModulusSuccess() {
    // Given
    var modulus =
        HexFormat.of()
            .parseHex(
                "9a277669023723947d0ebdccef967a24c715018df6ce66414fccd0f5bab54124b8caac6d7f52f8bbbab7de926b4f0ac4cc84793196e44928774a57737d0e4ee02962952257506e898846e353fa5fee31409a1d32124fb8df53d969dd7aa222866fa85e106f8a07e333d8ded4b10a8300b32d5f47cc5eab14033fa2bc0950b5c9");

    // When
    AuthCryptUtil.scrambleModulus(modulus);

    // Then
    assertEquals(
        "768ca46255674d1df5485e9f1556e7b0928f1cbfe481de9e1c15b928c01763a2d762f27d10d8ff58896f0046da4589c47fa926765abae23c7475f5cf745efb295fee3140023723947d0ebdccefccc0c6fb15018df6ce66414fccd0f5bab54124b8caac6d7f52f8bbbab7de926b4f0ac4cc84793196e44928774a57737d0e4ee0",
        HexFormat.of().formatHex(modulus));
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void scrambleModulusFail() {
    // Given
    var modulus = new byte[0];

    // When/Then
    AuthCryptUtil.scrambleModulus(modulus);
  }

  @Test
  public void scrambleInitSuccess() {
    // Given
    var buffer = HexFormat.of().parseHex("010203040506070800000000");
    var key = 0xDEADBEEF;

    // When
    AuthCryptUtil.scrambleInit(buffer, 0, 8, key);

    // Then
    assertEquals("01020304f1c2b3eef4c4b4e6", HexFormat.of().formatHex(buffer));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void scrambleInitOverflow() {
    // Given
    var buffer = HexFormat.of().parseHex("0102030405060708");
    var key = 0xdeadbeef;

    // When/Then
    AuthCryptUtil.scrambleInit(buffer, 0, buffer.length, key);
  }

  @Test
  public void calculateChecksum() {
    // Given
    var buffer = HexFormat.of().parseHex("0102030405060708");

    // When
    var checksum = AuthCryptUtil.calculateChecksum(buffer, 0, buffer.length);

    // Then
    assertEquals(0xc040404, checksum);
  }
}
