package com.github.goph3rx.transfer;

import java.util.Optional;

/** Adapter for the database that holds transfer information. */
public interface ITransferDatabase {
  /**
   * Create a new transfer to the game world.
   *
   * @param transfer Transfer details.
   */
  void create(Transfer transfer);

  /**
   * Fetch the transfer for the account.
   *
   * @param account Account name.
   * @return Transfer, if found.
   */
  Optional<Transfer> fetch(String account);

  /**
   * Delete the transfer for the account.
   *
   * @param account Account name.
   */
  void remove(String account);

  /**
   * Remove all expired transfers.
   *
   * @return Total number of transfers cleaned up.
   */
  int removeExpired();
}
