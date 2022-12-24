package com.github.goph3rx.auth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AuthCredentialKeyTest {
  @Test
  public void getModulus() {
    // Given
    var credentialKey = new AuthCredentialKey();

    // When
    var modulus = credentialKey.getModulus();

    // Then
    assertEquals(128, modulus.length);
  }

  @Test(expected = RuntimeException.class)
  public void decryptTooShort() {
    // Given
    var credentialKey = new AuthCredentialKey();
    var credentials = new byte[0];

    // When/Then
    credentialKey.decrypt(credentials);
  }

  @Test
  public void decryptSuccess() {
    // Given
    var credentialKey = new AuthCredentialKey();
    var credentials = new byte[128];

    // When/Then
    credentialKey.decrypt(credentials);
  }
}
