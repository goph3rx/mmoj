package com.github.goph3rx.game.messages;

import java.util.Arrays;
import java.util.HexFormat;

/**
 * First message from the server, starts the auth process.
 *
 * @param cryptKey Part of the encryption key for the traffic.
 */
public record ServerKeyPacket(byte[] cryptKey) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServerKeyPacket that = (ServerKeyPacket) o;
    return Arrays.equals(cryptKey, that.cryptKey);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(cryptKey);
  }

  @Override
  public String toString() {
    return "ServerKeyPacket[" + "cryptKey=" + HexFormat.of().formatHex(cryptKey) + ']';
  }
}
