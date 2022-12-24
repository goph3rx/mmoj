package com.github.goph3rx.auth;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.Cipher;

/** Encryption for credential keys. */
public class AuthCredentialKey implements IAuthCredentialKey {
  /** Name of the encryption algorithm in use. */
  private static final String ALGORITHM = "RSA/ECB/NoPadding";

  /** Key pair to use for encryption. */
  private final KeyPair key;
  /** Lock for synchronizing access to this instance. */
  private final ReentrantLock lock = new ReentrantLock();

  /** Generate a new credential key with the compatible parameters. */
  public AuthCredentialKey() {
    try {
      var generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4));
      key = generator.generateKeyPair();
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] getModulus() {
    lock.lock();
    try {
      // Extract the modulus and encode it
      var publicKey = (RSAPublicKey) key.getPublic();
      var modulus = publicKey.getModulus().toByteArray();

      // Java insists on encoding the sign along with the value
      // We need to get rid of it for compatibility
      if (modulus.length == 129) {
        var temp = new byte[128];
        System.arraycopy(modulus, 1, temp, 0, temp.length);
        modulus = temp;
      }

      return modulus;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void decrypt(byte[] credentials) {
    lock.lock();
    try {
      try {
        var cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
        cipher.doFinal(credentials, 0, credentials.length, credentials);
      } catch (GeneralSecurityException e) {
        throw new RuntimeException(e);
      }

    } finally {
      lock.unlock();
    }
  }
}
