package com.github.goph3rx.game;

/** Initial traffic encryption. */
public class GameNoCrypt implements IGameCrypt {
  @Override
  public void encrypt(byte[] buffer, int offset, int length) {
    // Initial crypt doesn't transform the data
  }

  @Override
  public void decrypt(byte[] buffer, int offset, int length) {
    // Initial crypt doesn't transform the data
  }
}
