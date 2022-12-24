package com.github.goph3rx.auth;

import static org.junit.Assert.assertEquals;

import com.github.goph3rx.auth.messages.*;
import com.github.goph3rx.world.World;
import java.net.InetAddress;
import java.nio.BufferUnderflowException;
import java.util.HexFormat;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class AuthCodecTest {
  private byte[] buffer;
  private AuthCodec codec;

  @Before
  public void setUp() {
    buffer = new byte[1024];
    codec = new AuthCodec();
  }

  @Test
  public void encodeInit() {
    // Given
    var message =
        new ServerInit(
            0xdeadbeef,
            HexFormat.of()
                .parseHex(
                    "9a277669023723947d0ebdccef967a24c715018df6ce66414fccd0f5bab54124b8caac6d7f52f8bbbab7de926b4f0ac4cc84793196e44928774a57737d0e4ee02962952257506e898846e353fa5fee31409a1d32124fb8df53d969dd7aa222866fa85e106f8a07e333d8ded4b10a8300b32d5f47cc5eab14033fa2bc0950b5c9"),
            HexFormat.of().parseHex("060708090a"));

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals(
        "00efbeadde21c60000768ca46255674d1df5485e9f1556e7b0928f1cbfe481de9e1c15b928c01763a2d762f27d10d8ff58896f0046da4589c47fa926765abae23c7475f5cf745efb295fee3140023723947d0ebdccefccc0c6fb15018df6ce66414fccd0f5bab54124b8caac6d7f52f8bbbab7de926b4f0ac4cc84793196e44928774a57737d0e4ee000000000000000000000000000000000060708090a",
        HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeGGAuth() {
    // Given
    var message = new ServerGGAuth(GGAuthResult.SKIP);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals(
        "0b0b00000000000000000000000000000000000000", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeLoginFail() {
    // Given
    var message = new ServerLoginFail(LoginFailReason.PASS_WRONG);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("0102000000", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeAccountKicked() {
    // Given
    var message = new ServerAccountKicked(AccountKickedReason.PERMANENTLY_BANNED);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("0220000000", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeLoginOk() {
    // Given
    var message = new ServerLoginOk(317328662928818516L);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals(
        "035401ecd6bc60670400000000000000000000000000000000000000000000000000000000000000000000000000000000",
        HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodeServerList() {
    // Given
    var message =
        new ServerServerList(
            1, List.of(new World(1, InetAddress.getLoopbackAddress(), 2106, 1000, 5000, true)));

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals(
        "040101017f0000013a0800000001e8038813010000000000",
        HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodePlayFail() {
    // Given
    var message = new ServerPlayFail(LoginFailReason.ACCESS_FAILED);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("0604000000", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test
  public void encodePlayOk() {
    // Given
    var message = new ServerPlayOk(1504341929038059147L);

    // When
    var length = codec.encode(message, buffer, 0);

    // Then
    assertEquals("078b160a9d037fe014", HexFormat.of().formatHex(buffer, 0, length));
  }

  @Test(expected = IllegalArgumentException.class)
  public void encodeInvalid() {
    // Given
    var message = new Object();

    // When/Then
    codec.encode(message, buffer, 0);
  }

  @Test
  public void decodeAuthGameGuard() {
    // Given
    var buffer = HexFormat.of().parseHex("0725c7892400000000000000000000000000000000000000");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(new ClientAuthGameGuard(), message);
  }

  @Test
  public void decodeRequestAuthLogin() {
    // Given
    var buffer =
        HexFormat.of()
            .parseHex(
                "007e6c5a765631f7102c30505df2ae630b725e25f9f95ac1a66330c2074b229598d75e48d30f8848d8c30cbbc9d2a6e36ab502cc028fccba58a6cbfb9d9164ea13129d03eefff00e383d38694e2b0e7225bb576f6e4d37097c6299b7bb06c47e29b7f2a48ad11781eb93c039e3f9f9f7d63d91bbf5b8ab7dd7f038a83dc22cb3000b");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(
        new ClientRequestAuthLogin(
            HexFormat.of()
                .parseHex(
                    "7e6c5a765631f7102c30505df2ae630b725e25f9f95ac1a66330c2074b229598d75e48d30f8848d8c30cbbc9d2a6e36ab502cc028fccba58a6cbfb9d9164ea13129d03eefff00e383d38694e2b0e7225bb576f6e4d37097c6299b7bb06c47e29b7f2a48ad11781eb93c039e3f9f9f7d63d91bbf5b8ab7dd7f038a83dc22cb300")),
        message);
  }

  @Test
  public void decodeRequestServerList() {
    // Given
    var buffer = HexFormat.of().parseHex("05e30568bbe125f82704");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(new ClientRequestServerList(2880093613145458147L), message);
  }

  @Test
  public void decodeRequestServerLogin() {
    // Given
    var buffer = HexFormat.of().parseHex("028e92b0e741062dd401");

    // When
    var message = codec.decode(buffer, 0, buffer.length);

    // Then
    assertEquals(new ClientRequestServerLogin(-3157860883587100018L, 1), message);
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
    var buffer = HexFormat.of().parseHex("00");

    // When/Then
    codec.decode(buffer, 0, buffer.length);
  }
}
