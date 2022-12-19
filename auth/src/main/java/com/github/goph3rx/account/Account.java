package com.github.goph3rx.account;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Account record.
 *
 * @param username Name of the account.
 * @param salt Base64 representation of the randomly chosen salt.
 * @param password Base64 representation of the password hash.
 * @param lastWorld Last world that this account accessed.
 * @param bannedUntil If set, blocks the account until given time.
 */
public record Account(
    String username,
    String salt,
    String password,
    int lastWorld,
    Optional<LocalDateTime> bannedUntil) {

  @Override
  public String toString() {
    return "Account["
        + "username='"
        + username
        + '\''
        + ", lastWorld="
        + lastWorld
        + ", bannedUntil="
        + bannedUntil
        + ']';
  }
}
