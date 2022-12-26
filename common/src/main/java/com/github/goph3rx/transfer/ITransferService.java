package com.github.goph3rx.transfer;

/** Service for world transfers. */
public interface ITransferService {
  /**
   * Generate a transfer to the game world.
   *
   * @param account Account name.
   * @return Transfer details.
   */
  Transfer generate(String account);

  /**
   * Complete the transfer to the game world.
   *
   * @param account Account name.
   * @param auth First part of the token.
   * @param play Second part of the token.
   * @return Flag to indicate success.
   */
  boolean complete(String account, long auth, long play);

  /** Start the background tasks for this service. */
  void start();
}
