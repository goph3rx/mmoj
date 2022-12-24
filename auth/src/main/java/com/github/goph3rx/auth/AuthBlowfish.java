package com.github.goph3rx.auth;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Blowfish traffic encryption. Does byte flipping before and after encryption for compatibility
 * with the game old crypt implementation.
 */
public class AuthBlowfish implements IAuthCrypt {
  /** Name of the encryption algorithm in use. */
  private static final String ALGORITHM = "Blowfish/ECB/NoPadding";
  /** Initial key for encryption. */
  private static final byte[] INIT_KEY =
      HexFormat.of().parseHex("6b60cb5b82ce90b1cc2b6c556c6c6c6c");

  /** Encryption. */
  private final Cipher encrypt;
  /** Decryption. */
  private final Cipher decrypt;

  @Override
  public int getBlockSize() {
    return encrypt.getBlockSize();
  }

  /** Initialise with the default encryption key. */
  public AuthBlowfish() {
    this(INIT_KEY);
  }

  /**
   * Initialise with the given encryption key.
   *
   * @param key Encryption key.
   */
  public AuthBlowfish(byte[] key) {
    var keySpec = new SecretKeySpec(key, "Blowfish");
    try {
      encrypt = Cipher.getInstance(ALGORITHM);
      encrypt.init(Cipher.ENCRYPT_MODE, keySpec);
      decrypt = Cipher.getInstance(ALGORITHM);
      decrypt.init(Cipher.DECRYPT_MODE, keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int encrypt(byte[] buffer, int offset, int length) throws ShortBufferException {
    compat(buffer, offset, length);
    var encrypted = encrypt.update(buffer, offset, length, buffer, offset);
    compat(buffer, offset, length);
    return encrypted;
  }

  @Override
  public int decrypt(byte[] buffer, int offset, int length) throws ShortBufferException {
    compat(buffer, offset, length);
    var decrypted = decrypt.update(buffer, offset, length, buffer, offset);
    compat(buffer, offset, length);
    return decrypted;
  }

  /**
   * Flip the order of bytes for compatibility.
   *
   * @param buffer Buffer to operate on.
   */
  private static void compat(byte[] buffer, int offset, int length) {
    var end = offset + length;
    for (; offset < end; offset += AuthCryptUtil.BLOCK_SIZE) {
      var tmp = buffer[offset];
      buffer[offset] = buffer[offset + 3];
      buffer[offset + 3] = tmp;

      tmp = buffer[offset + 1];
      buffer[offset + 1] = buffer[offset + 2];
      buffer[offset + 2] = tmp;
    }
  }
}
