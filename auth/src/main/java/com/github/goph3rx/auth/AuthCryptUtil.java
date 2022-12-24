package com.github.goph3rx.auth;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Utilities for encryption. */
public class AuthCryptUtil {
  /** Block size for various encryption routines. */
  public static final int BLOCK_SIZE = 4;

  /**
   * Scramble the modulus for username/password encryption.
   *
   * @param modulus Modulus.
   */
  public static void scrambleModulus(byte[] modulus) {
    for (var i = 0; i < 4; i++) {
      var tmp = modulus[i];
      modulus[i] = modulus[i + 77];
      modulus[i + 77] = tmp;
    }

    for (var i = 0; i < 64; i++) {
      modulus[i] = (byte) (modulus[i] ^ modulus[i + 64]);
    }

    for (var i = 0; i < 4; i++) {
      modulus[i + 13] = (byte) (modulus[i + 13] ^ modulus[i + 52]);
    }

    for (var i = 0; i < 64; i++) {
      modulus[i + 64] = (byte) (modulus[i + 64] ^ modulus[i]);
    }
  }

  /**
   * Scramble the initial message from the server.
   *
   * @param buffer Buffer with the message.
   * @param offset Position to start at.
   * @param length Length of data.
   * @param key Encryption key.
   * @return New length of the data.
   */
  public static int scrambleInit(byte[] buffer, int offset, int length, int key) {
    // Scramble
    var wrapped = ByteBuffer.wrap(buffer, 0, buffer.length).order(ByteOrder.LITTLE_ENDIAN);
    var end = offset + length;
    offset += BLOCK_SIZE;
    for (; offset < end; offset += BLOCK_SIZE) {

      var block = wrapped.getInt(offset);
      key += block;
      block ^= key;
      wrapped.putInt(offset, block);
    }

    // Write the key
    wrapped.putInt(offset, key);
    return offset + BLOCK_SIZE;
  }

  /**
   * Calculate the checksum for the buffer.
   *
   * @param buffer Buffer with the message.
   * @param offset Position to start at.
   * @param length Length of data.
   * @return Checksum.
   */
  public static int calculateChecksum(byte[] buffer, int offset, int length) {
    var wrapped = ByteBuffer.wrap(buffer, 0, buffer.length).order(ByteOrder.LITTLE_ENDIAN);
    var end = offset + length;
    var checksum = 0;
    for (; offset < end; offset += BLOCK_SIZE) {
      var block = wrapped.getInt(offset);
      checksum ^= block;
    }
    return checksum;
  }

  private AuthCryptUtil() {
    throw new IllegalStateException("Utility class");
  }
}
