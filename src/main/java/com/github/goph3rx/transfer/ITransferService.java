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
}
