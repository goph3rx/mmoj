package com.github.goph3rx.game;

import com.github.goph3rx.character.CharacterGender;
import com.github.goph3rx.character.CharacterRace;
import com.github.goph3rx.game.messages.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

/** Encoding and decoding of messages. */
public class GameCodec implements IGameCodec {
  /** Size of the appearance block for the character. */
  private static final int APPEARANCE_SIZE = 12;

  @Override
  public int encode(Object message, byte[] buffer, int offset) {
    var wrapped =
        ByteBuffer.wrap(buffer, offset, buffer.length - offset).order(ByteOrder.LITTLE_ENDIAN);
    switch (message) {
      case ServerKeyPacket m -> encode(m, wrapped);
      case ServerAuthLoginFail m -> encode(m, wrapped);
      case ServerCharSelectInfo m -> encode(m, wrapped);
      case ServerCharTemplates m -> encode(m, wrapped);
      case ServerCharCreateFail m -> encode(m, wrapped);
      case ServerCharCreateOk m -> encode(m, wrapped);
      case ServerCharDeleteFail m -> encode(m, wrapped);
      case ServerCharDeleteOk m -> encode(m, wrapped);
      default -> throw new IllegalArgumentException("Cannot encode message");
    }
    return wrapped.position();
  }

  @Override
  public Object decode(byte[] buffer, int offset, int length) {
    var wrapped = ByteBuffer.wrap(buffer, offset, length - offset).order(ByteOrder.LITTLE_ENDIAN);
    var id = wrapped.get() & 0xff;
    return switch (id) {
      case 0x0c -> decodeCharCreate(wrapped);
      case 0x0d -> decodeCharDelete(wrapped);
      case 0x0e -> decodeProtocolVersion(wrapped);
      case 0x13 -> new ClientNewCharacter();
      case 0x2b -> decodeAuthLogin(wrapped);
      case 0xd0 -> decodeExtended(wrapped);
      case 0x7b -> decodeCharRestore(wrapped);
      default -> throw new IllegalArgumentException(
          "Cannot decode message with id=%h".formatted(id));
    };
  }

  /**
   * Decode the extended operation ID.
   *
   * @param buffer Buffer to read from.
   * @return Decoded message.
   */
  private Object decodeExtended(ByteBuffer buffer) {
    var id = buffer.getShort() & 0xffff;
    return switch (id) {
      case 0x36 -> new ClientRequestGotoLobby();
      default -> throw new IllegalArgumentException(
          "Cannot decode message with extended id=%h".formatted(id));
    };
  }

  /**
   * Read a string in the format compatible with the game protocol.
   *
   * @param buffer Buffer to read from.
   * @return Resulting string.
   */
  private String getString(ByteBuffer buffer) {
    var start = buffer.position();
    while (true) {
      var ch = buffer.getShort();
      if (ch == 0) {
        break;
      }
    }
    var end = buffer.position() - 2;
    return new String(
        buffer.array(), buffer.arrayOffset() + start, end - start, StandardCharsets.UTF_16LE);
  }

  /**
   * Write a string in the format compatible with the game protocol.
   *
   * @param buffer Buffer to write to.
   * @param value String value to write.
   */
  private void putString(ByteBuffer buffer, String value) {
    buffer.put(value.getBytes(StandardCharsets.UTF_16LE)).putShort((short) 0);
  }

  private void encode(ServerKeyPacket message, ByteBuffer buffer) {
    buffer
        .put((byte) 0x2e)
        .put((byte) 1)
        .put(message.cryptKey())
        .putInt(1)
        .putInt(1)
        .put((byte) 1)
        .putInt(0);
  }

  private void encode(ServerAuthLoginFail message, ByteBuffer buffer) {
    buffer.put((byte) 0x0a).putInt(message.reason().code);
  }

  private void encode(ServerCharSelectInfo message, ByteBuffer buffer) {
    var now = LocalDateTime.now();
    var active =
        message.characters().stream()
            .filter(c -> c.general().lastUsedOn().isPresent())
            .max(Comparator.comparing(c -> c.general().lastUsedOn().get()));
    buffer.put((byte) 0x09).putInt(message.characters().size()).putInt(7).put((byte) 0);

    for (var character : message.characters()) {
      var deleted =
          character.general().deleteOn().map(d -> ChronoUnit.SECONDS.between(now, d)).orElse(0L);

      putString(buffer, character.general().name());
      buffer.putInt(character.objectId());
      putString(buffer, character.general().account());

      buffer
          .putInt(0)
          .putInt(0) // TODO: Clan
          .putInt(0); // TODO: Alliance

      buffer
          .putInt(character.general().gender().code)
          .putInt(character.general().race().code)
          .putInt(character.general().clazz());

      buffer.putInt(1).putInt(0).putInt(0).putInt(0);

      buffer
          .putDouble(100) // TODO: Current HP
          .putDouble(100) // TODO: Current MP
          .putInt(0) // TODO: SP
          .putLong(0) // TODO: Exp
          .putInt(1) // TODO: Level
          .putInt(0) // TODO: Karma
          .putInt(0) // TODO: PK
          .putInt(0); // TODO: PvP

      buffer.putInt(0).putInt(0).putInt(0).putInt(0).putInt(0).putInt(0).putInt(0);

      // TODO: Items
      for (var i = 0; i < 25; i++) {
        buffer.putInt(0);
      }

      buffer.put(character.general().appearance());
      buffer.putInt(0);

      buffer
          .putDouble(100) // TODO: Max HP
          .putDouble(100); // TODO: Max MP

      buffer.putInt(Math.toIntExact(deleted));
      buffer.putInt(character.general().clazz()); // TODO: Class
      buffer.putInt(active.map(c -> c == character).orElse(false) ? 1 : 0);
      buffer
          .put((byte) 0) // TODO: Enchantment
          .putInt(0) // TODO: Augmentation
          .putInt(0); // TODO: Transformation
    }
  }

  private void encode(ServerCharTemplates message, ByteBuffer buffer) {
    buffer.put((byte) 0x0d).putInt(message.templates().size());
    for (var template : message.templates()) {
      buffer
          .putInt(template.race().code)
          .putInt(template.clazz())
          // STR
          .putInt(70)
          .putInt(40)
          .putInt(10)
          // DEX
          .putInt(70)
          .putInt(40)
          .putInt(10)
          // CON
          .putInt(70)
          .putInt(40)
          .putInt(10)
          // INT
          .putInt(70)
          .putInt(40)
          .putInt(10)
          // WIT
          .putInt(70)
          .putInt(40)
          .putInt(10)
          // MEN
          .putInt(70)
          .putInt(40)
          .putInt(10);
    }
  }

  private void encode(ServerCharCreateFail message, ByteBuffer buffer) {
    buffer.put((byte) 0x10).putInt(message.reason().code);
  }

  @SuppressWarnings("unused")
  private void encode(ServerCharCreateOk message, ByteBuffer buffer) {
    buffer.put((byte) 0x0f).putInt(1);
  }

  private void encode(ServerCharDeleteFail message, ByteBuffer buffer) {
    buffer.put((byte) 0x1e).putInt(message.reason().code);
  }

  @SuppressWarnings("unused")
  private void encode(ServerCharDeleteOk message, ByteBuffer buffer) {
    buffer.put((byte) 0x1d);
  }

  private ClientProtocolVersion decodeProtocolVersion(ByteBuffer buffer) {
    return new ClientProtocolVersion(buffer.getInt());
  }

  private ClientAuthLogin decodeAuthLogin(ByteBuffer buffer) {
    var account = getString(buffer);
    var play = (long) buffer.getInt();
    play <<= 32;
    play |= buffer.getInt() & 0xFFFFFFFFL;
    var auth = buffer.getLong();
    return new ClientAuthLogin(account, auth, play);
  }

  private ClientCharCreate decodeCharCreate(ByteBuffer buffer) {
    // Key parameters
    var name = getString(buffer);
    var race = CharacterRace.valueOf(buffer.getInt());
    var gender = CharacterGender.valueOf(buffer.getInt());
    var clazz = buffer.getInt();
    // Skip INT/STR/CON/MEN/DEX/WIT
    buffer.position(buffer.position() + 24);
    // Appearance
    var appearance = new byte[APPEARANCE_SIZE];
    buffer.get(appearance);
    return new ClientCharCreate(name, race, gender, clazz, appearance);
  }

  private ClientCharDelete decodeCharDelete(ByteBuffer buffer) {
    return new ClientCharDelete(buffer.getInt());
  }

  private ClientCharRestore decodeCharRestore(ByteBuffer buffer) {
    return new ClientCharRestore(buffer.getInt());
  }
}
