package com.github.goph3rx.game;

import static org.junit.Assert.assertEquals;

import com.github.goph3rx.character.CharacterGender;
import com.github.goph3rx.character.CharacterRace;
import com.github.goph3rx.character.CharacterTemplate;
import com.github.goph3rx.game.messages.*;
import java.nio.BufferUnderflowException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class GameCodecTest {
  private byte[] buffer;
  private GameCodec codec;

  @Before
  public void setUp() {
    buffer = new byte[4096];
    codec = new GameCodec();
  }

  @Test
  public void encodeKeyPacket() {
    // Given
    var cryptKey = HexFormat.of().parseHex("83a5ef0a3caab8af");
    var message = new ServerKeyPacket(cryptKey);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals(
        "2e0183a5ef0a3caab8af01000000010000000100000000",
        HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeAuthLoginFail() {
    // Given
    var message = new ServerAuthLoginFail(AuthLoginFailReason.ACCESS_FAILED_TRY_LATER);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("0a04000000", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeCharTemplates() {
    // Given
    var message =
        new ServerCharTemplates(
            List.of(new CharacterTemplate(CharacterRace.HUMAN, 0, Optional.empty())));

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals(
        "0d01000000000000000000000046000000280000000a00000046000000280000000a00000046000000280000000a00000046000000280000000a00000046000000280000000a00000046000000280000000a000000",
        HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeCharCreateFail() {
    // Given
    var message = new ServerCharCreateFail(CharCreateFailReason.SIXTEEN_ENG_CHARS);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("1003000000", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeCharCreateOk() {
    // Given
    var message = new ServerCharCreateOk();

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("0f01000000", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeCharDeleteFail() {
    // Given
    var message = new ServerCharDeleteFail(CharDeleteFailReason.DELETION_FAILED);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("1e01000000", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeCharDeleteOk() {
    // Given
    var message = new ServerCharDeleteOk();

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("1d", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test(expected = IllegalArgumentException.class)
  public void encodeInvalid() {
    // Given
    var message = new Object();

    // When/Then
    codec.encode(message, buffer, 0);
  }

  @Test
  public void decodeProtocolVersion() {
    // Given
    var buffer = HexFormat.of().parseHex("0e53000000");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(new ClientProtocolVersion(83), message);
  }

  @Test
  public void decodeAuthLogin() {
    // Given
    var buffer =
        HexFormat.of()
            .parseHex(
                "2b7400650073007400000076b6e579670290c1ef9e144baa9a555c010000000000000000000000");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(new ClientAuthLogin("test", 6653394080704536303L, 8783627269425857127L), message);
  }

  @Test
  public void decodeNewCharacter() {
    // Given
    var buffer = HexFormat.of().parseHex("13");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(new ClientNewCharacter(), message);
  }

  @Test
  public void decodeCharCreate() {
    // Given
    var buffer =
        HexFormat.of()
            .parseHex(
                "0c4b0061006d00610065006c00000005000000010000007c000000280000002800000028000000280000002800000028000000060000000000000002000000");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(
        new ClientCharCreate(
            "Kamael",
            CharacterRace.KAMAEL,
            CharacterGender.FEMALE,
            124,
            HexFormat.of().parseHex("060000000000000002000000")),
        message);
  }

  @Test
  public void decodeRequestGotoLobby() {
    // Given
    var buffer = HexFormat.of().parseHex("d03600");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(new ClientRequestGotoLobby(), message);
  }

  @Test
  public void decodeCharDelete() {
    // Given
    var buffer = HexFormat.of().parseHex("0d06000000");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(new ClientCharDelete(6), message);
  }

  @Test(expected = IllegalArgumentException.class)
  public void decodeInvalid() {
    // Given
    var buffer = HexFormat.of().parseHex("ff");

    // When/Then
    codec.decode(buffer, 0, buffer.length);
  }

  @Test(expected = BufferUnderflowException.class)
  public void decodeIncomplete() {
    // Given
    var buffer = HexFormat.of().parseHex("0e");

    // When/Then
    codec.decode(buffer, 0, buffer.length);
  }
}
