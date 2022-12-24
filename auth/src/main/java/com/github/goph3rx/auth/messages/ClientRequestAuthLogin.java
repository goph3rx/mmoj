package com.github.goph3rx.auth.messages;

import java.util.Arrays;
import java.util.HexFormat;

/**
 * Request for ordinary auth.
 *
 * @param credentials Encrypted username and password.
 */
public record ClientRequestAuthLogin(byte[] credentials) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientRequestAuthLogin that = (ClientRequestAuthLogin) o;
    return Arrays.equals(credentials, that.credentials);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(credentials);
  }

  @Override
  public String toString() {
    return "ClientRequestAuthLogin[" + "credentials=" + HexFormat.of().formatHex(credentials) + ']';
  }
}
