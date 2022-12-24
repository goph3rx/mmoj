package com.github.goph3rx.auth;

/** Encryption for credential keys. */
public interface IAuthCredentialKey {
  /** Get the modulus for the credential key. */
  byte[] getModulus();

  /**
   * Decrypt the credentials in place.
   *
   * @param credentials Credentials.
   */
  void decrypt(byte[] credentials);
}
