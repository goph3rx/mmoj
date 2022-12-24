package com.github.goph3rx.auth;

/** Encoding and decoding of messages. */
public interface IAuthCodec {
  /**
   * Encode the message into the buffer.
   *
   * @param message Message.
   * @param buffer Buffer.
   * @param offset Position to start at.
   * @return Length of the encoded data.
   */
  int encode(Object message, byte[] buffer, int offset);

  /**
   * Decode the message from the buffer.
   *
   * @param buffer Buffer.
   * @param offset Position to start at.
   * @param length Length of data.
   * @return Decoded message.
   */
  Object decode(byte[] buffer, int offset, int length);
}
