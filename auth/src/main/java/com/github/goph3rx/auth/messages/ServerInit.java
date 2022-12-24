package com.github.goph3rx.auth.messages;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * First message from the server, starts the auth process.
 *
 * @param sessionId Session identifier.
 * @param modulus Modulus for username/password encryption.
 * @param cryptKey Key for traffic encryption.
 */
public record ServerInit(int sessionId, byte[] modulus, byte[] cryptKey) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServerInit that = (ServerInit) o;
    return sessionId == that.sessionId
        && Arrays.equals(modulus, that.modulus)
        && Arrays.equals(cryptKey, that.cryptKey);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(sessionId);
    result = 31 * result + Arrays.hashCode(modulus);
    result = 31 * result + Arrays.hashCode(cryptKey);
    return result;
  }

  @Override
  public String toString() {
    return "ServerInit["
        + "sessionId="
        + sessionId
        + ", modulus="
        + HexFormat.of().formatHex(modulus)
        + ", cryptKey="
        + HexFormat.of().formatHex(cryptKey)
        + ']';
  }
}
