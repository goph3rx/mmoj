package com.github.goph3rx.game;

import java.util.HexFormat;

/** Traffic encryption. */
public class GameCrypt implements IGameCrypt {
  /** Offset for updating the key. */
  private static final int KEY_ROTATE_OFFSET = 8;
  /** First part of the crypt key. */
  private static final byte[] INIT_KEY = HexFormat.of().parseHex("c8279301a16c3197");

  /** Encryption key. */
  private final byte[] encryptKey = new byte[16];
  /** Decryption key. */
  private final byte[] decryptKey = new byte[16];

  /**
   * Initialize the crypt engine.
   *
   * @param cryptKey Crypt key.
   */
  public GameCrypt(byte[] cryptKey) {
    System.arraycopy(cryptKey, 0, encryptKey, 0, cryptKey.length);
    System.arraycopy(cryptKey, 0, decryptKey, 0, cryptKey.length);
    System.arraycopy(INIT_KEY, 0, encryptKey, cryptKey.length, INIT_KEY.length);
    System.arraycopy(INIT_KEY, 0, decryptKey, cryptKey.length, INIT_KEY.length);
  }

  @Override
  public void encrypt(byte[] buffer, int offset, int length) {
    // Encrypt
    var prev = (byte) 0;
    for (var i = offset; i < offset + length; i++) {
      prev = (byte) (prev ^ buffer[i] ^ encryptKey[(i - offset) & (encryptKey.length - 1)]);
      buffer[i] = prev;
    }

    // Update the key
    rotateKey(encryptKey, length);
  }

  @Override
  public void decrypt(byte[] buffer, int offset, int length) {
    // Decrypt
    var prev = (byte) 0;
    for (var i = offset; i < offset + length; i++) {
      var temp = buffer[i];
      buffer[i] = (byte) (prev ^ temp ^ decryptKey[(i - offset) & (decryptKey.length - 1)]);
      prev = temp;
    }

    // Update the key
    rotateKey(decryptKey, length);
  }

  /**
   * Update the crypt key.
   *
   * @param cryptKey Crypt key.
   * @param dataLength Size of the processed buffer.
   */
  private void rotateKey(byte[] cryptKey, int dataLength) {
    int block = cryptKey[KEY_ROTATE_OFFSET + 3] & 0xFF;
    block <<= 8;
    block |= cryptKey[KEY_ROTATE_OFFSET + 2] & 0xFF;
    block <<= 8;
    block |= cryptKey[KEY_ROTATE_OFFSET + 1] & 0xFF;
    block <<= 8;
    block |= cryptKey[KEY_ROTATE_OFFSET] & 0xFF;

    block += dataLength;

    cryptKey[KEY_ROTATE_OFFSET] = (byte) block;
    cryptKey[KEY_ROTATE_OFFSET + 1] = (byte) (block >> 8);
    cryptKey[KEY_ROTATE_OFFSET + 2] = (byte) (block >> 16);
    cryptKey[KEY_ROTATE_OFFSET + 3] = (byte) (block >> 24);
  }
}
