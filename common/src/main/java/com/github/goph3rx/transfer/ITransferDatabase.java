package com.github.goph3rx.transfer;

/** Adapter for the database that holds transfer information. */
public interface ITransferDatabase {
  /**
   * Create a new transfer to the game world.
   *
   * @param transfer Transfer details.
   */
  void create(Transfer transfer);
}
