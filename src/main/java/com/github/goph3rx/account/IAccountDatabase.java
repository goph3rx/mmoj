package com.github.goph3rx.account;

import java.util.Optional;

/** Adapter for the database that holds account information. */
public interface IAccountDatabase {
  /**
   * Fetch the account.
   *
   * @param username Account username.
   * @return Account record, if found.
   */
  Optional<Account> fetch(String username);
}
