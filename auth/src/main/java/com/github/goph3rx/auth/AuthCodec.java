package com.github.goph3rx.auth;

import com.github.goph3rx.auth.messages.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Encoding and decoding of messages. */
public class AuthCodec implements IAuthCodec {
  /** Version of the protocol implemented by this codec. */
  public static final int PROTOCOL_VERSION = 0xc621;

  @Override
  public int encode(Object message, byte[] buffer, int offset) {
    var wrapped =
        ByteBuffer.wrap(buffer, offset, buffer.length - offset).order(ByteOrder.LITTLE_ENDIAN);
    switch (message) {
      case ServerInit m -> encode(m, wrapped);
      case ServerGGAuth m -> encode(m, wrapped);
      case ServerLoginFail m -> encode(m, wrapped);
      case ServerAccountKicked m -> encode(m, wrapped);
      case ServerLoginOk m -> encode(m, wrapped);
      case ServerServerList m -> encode(m, wrapped);
      case ServerPlayFail m -> encode(m, wrapped);
      case ServerPlayOk m -> encode(m, wrapped);
      default -> throw new IllegalArgumentException("Cannot encode message");
    }
    return wrapped.position();
  }

  @Override
  public Object decode(byte[] buffer, int offset, int length) {
    var wrapped = ByteBuffer.wrap(buffer, offset, length - offset).order(ByteOrder.LITTLE_ENDIAN);
    var id = wrapped.get();
    return switch (id) {
      case 0x00 -> decodeRequestAuthLogin(wrapped);
      case 0x02 -> decodeRequestServerLogin(wrapped);
      case 0x05 -> decodeRequestServerList(wrapped);
      case 0x07 -> new ClientAuthGameGuard();
      default -> throw new IllegalArgumentException(
          "Cannot decode message with id=%d".formatted(id));
    };
  }

  private void encode(ServerInit message, ByteBuffer buffer) {
    AuthCryptUtil.scrambleModulus(message.modulus());
    buffer
        .put((byte) 0x00)
        .putInt(message.sessionId())
        .putInt(PROTOCOL_VERSION)
        .put(message.modulus())
        .putLong(0)
        .putLong(0)
        .put(message.cryptKey());
  }

  private void encode(ServerGGAuth message, ByteBuffer buffer) {
    buffer.put((byte) 0x0b).putInt(message.result().code).putLong(0).putLong(0);
  }

  private void encode(ServerLoginFail message, ByteBuffer buffer) {
    buffer.put((byte) 0x01).putInt(message.reason().code);
  }

  private void encode(ServerAccountKicked message, ByteBuffer buffer) {
    buffer.put((byte) 0x02).putInt(message.reason().code);
  }

  private void encode(ServerLoginOk message, ByteBuffer buffer) {
    buffer
        .put((byte) 0x03)
        .putLong(message.authToken())
        .putLong(0)
        .putLong(0)
        .putLong(0)
        .putLong(0)
        .putLong(0);
  }

  private void encode(ServerServerList message, ByteBuffer buffer) {
    buffer.put((byte) 0x04);
    buffer.put((byte) message.worlds().size());
    buffer.put((byte) message.lastWorld());
    for (var world : message.worlds()) {
      buffer
          .put((byte) world.id())
          .put(world.ip().getAddress())
          .putInt(world.port())
          .put((byte) 0)
          .put((byte) 1)
          .putShort((short) world.currentPlayers())
          .putShort((short) world.maximumPlayers())
          .put((byte) (world.isOnline() ? 1 : 0))
          .putInt(0)
          .put((byte) 0);
    }
  }

  private void encode(ServerPlayFail message, ByteBuffer buffer) {
    buffer.put((byte) 0x06).putInt(message.reason().code);
  }

  private void encode(ServerPlayOk message, ByteBuffer buffer) {
    buffer.put((byte) 0x07).putLong(message.playToken());
  }

  private ClientRequestAuthLogin decodeRequestAuthLogin(ByteBuffer buffer) {
    var credentials = new byte[128];
    buffer.get(credentials);
    return new ClientRequestAuthLogin(credentials);
  }

  private ClientRequestServerList decodeRequestServerList(ByteBuffer buffer) {
    return new ClientRequestServerList(buffer.getLong());
  }

  private ClientRequestServerLogin decodeRequestServerLogin(ByteBuffer buffer) {
    return new ClientRequestServerLogin(buffer.getLong(), buffer.get());
  }
}
