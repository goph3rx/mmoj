package com.github.goph3rx.account;

import java.util.Optional;

/** Service for managing accounts. */
public interface IAccountService {
  /**
   * Find the account.
   *
   * @param username Account username.
   * @param password Account password.
   * @return Account, if found.
   */
  Optional<Account> find(String username, String password);

  /**
   * Update the last world entered.
   *
   * @param username Account username.
   * @param lastWorld Last world.
   */
  void setLastWorld(String username, int lastWorld);
}
