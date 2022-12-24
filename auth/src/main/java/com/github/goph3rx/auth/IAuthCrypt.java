package com.github.goph3rx.auth;

import javax.crypto.ShortBufferException;

/** Traffic encryption. */
public interface IAuthCrypt {
  /** Get the block size for the cipher. */
  int getBlockSize();

  /**
   * Encrypt the buffer in place.
   *
   * @param buffer Buffer.
   * @param offset Position to start at.
   * @param length Length of data.
   * @return Length of encrypted data.
   * @throws ShortBufferException Output cannot fit in the buffer provided.
   */
  int encrypt(byte[] buffer, int offset, int length) throws ShortBufferException;

  /**
   * Decrypt the buffer in place.
   *
   * @param buffer Buffer.
   * @param offset Position to start at.
   * @param length Length of data.
   * @return Length of decrypted data.
   * @throws ShortBufferException Output cannot fit in the buffer provided.
   */
  int decrypt(byte[] buffer, int offset, int length) throws ShortBufferException;
}
