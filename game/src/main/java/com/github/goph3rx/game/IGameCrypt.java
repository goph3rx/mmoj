package com.github.goph3rx.game;

/** Traffic encryption. */
public interface IGameCrypt {
  /**
   * Encrypt the buffer in place.
   *
   * @param buffer Buffer.
   * @param offset Position to start at.
   * @param length Length of data.
   */
  void encrypt(byte[] buffer, int offset, int length);

  /**
   * Decrypt the buffer in place.
   *
   * @param buffer Buffer.
   * @param offset Position to start at.
   * @param length Length of data.
   */
  void decrypt(byte[] buffer, int offset, int length);
}
