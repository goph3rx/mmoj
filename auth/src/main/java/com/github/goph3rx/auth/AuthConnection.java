package com.github.goph3rx.auth;

import com.github.goph3rx.auth.messages.ServerInit;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.ShortBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Connection to the auth server. */
public class AuthConnection implements IAuthConnection {
  /** Size of the IO buffers for the client. */
  private static final int BUFFER_SIZE = 1024;
  /** Size of the packet header. */
  private static final int HEADER_SIZE = 2;
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(AuthConnection.class);

  /** Socket for communicating with the client. */
  private final Socket socket;
  /** Buffer for writing and sending messages. */
  private final byte[] write = new byte[BUFFER_SIZE];
  /** Buffer for reading messages. */
  private final byte[] read = new byte[BUFFER_SIZE];
  /** Encoding messages. */
  private final IAuthCodec codec;
  /** Key for encrypting the first message. */
  private final int scrambleKey;
  /** Lock for synchronizing access to the encryption engine. */
  private final ReentrantLock cryptLock = new ReentrantLock();
  /** Lock for synchronizing sending of messages to the client. */
  private final ReentrantLock sendLock = new ReentrantLock();
  /** Traffic encryption. */
  private IAuthCrypt crypt;

  @Override
  public SocketAddress getRemoteAddress() {
    return socket.getRemoteSocketAddress();
  }

  /**
   * Create a new connection.
   *
   * @param socket Socket for communication.
   * @param codec Encoding messages.
   * @param crypt Traffic encryption.
   * @param scrambleKey Key for encrypting the first message.
   */
  public AuthConnection(Socket socket, IAuthCodec codec, IAuthCrypt crypt, int scrambleKey) {
    this.socket = socket;
    this.codec = codec;
    this.scrambleKey = scrambleKey;
    this.crypt = crypt;
  }

  @Override
  public void send(Object message) throws IOException {
    sendLock.lock();
    try {
      logger.debug("Sending {}", message);

      // Reset the buffer
      Arrays.fill(write, (byte) 0);

      // Encode the message
      var length = codec.encode(message, write, HEADER_SIZE);

      // Padding
      var pad = length % AuthCryptUtil.BLOCK_SIZE;
      if (pad != 0) {
        length += AuthCryptUtil.BLOCK_SIZE - pad;
      }

      // Checksum and additional encryption
      if (message instanceof ServerInit) {
        length += 4;
        length = AuthCryptUtil.scrambleInit(write, HEADER_SIZE, length, scrambleKey);
      } else {
        var checksum = AuthCryptUtil.calculateChecksum(write, HEADER_SIZE, length);
        ByteBuffer.wrap(write, length, AuthCryptUtil.BLOCK_SIZE)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(checksum);
        length += 4;
      }

      // Encryption
      cryptLock.lock();
      try {
        // Pad
        pad = length % crypt.getBlockSize();
        length += crypt.getBlockSize() - pad;

        // Encrypt
        try {
          length = crypt.encrypt(write, HEADER_SIZE, length);
        } catch (ShortBufferException e) {
          throw new RuntimeException(e);
        }

        // Init message changes the encryption key
        if (message instanceof ServerInit init) {
          if (logger.isDebugEnabled()) {
            logger.debug(
                "Changing encryption key to {}", HexFormat.of().formatHex(init.cryptKey()));
          }
          crypt = new AuthBlowfish(init.cryptKey());
        }
      } finally {
        cryptLock.unlock();
      }

      // Write header
      ByteBuffer.wrap(write, 0, HEADER_SIZE)
          .order(ByteOrder.LITTLE_ENDIAN)
          .putShort((short) (length + HEADER_SIZE));

      // Send the packet off
      socket.getOutputStream().write(write, 0, length + HEADER_SIZE);
    } finally {
      sendLock.unlock();
    }
  }

  @Override
  public Optional<Object> receive() throws IOException {
    // Reset the buffer
    Arrays.fill(read, (byte) 0);

    var length = 0;
    try {
      // Read the header
      var input = socket.getInputStream();
      if (input.readNBytes(read, 0, HEADER_SIZE) != HEADER_SIZE) {
        logger.debug("Reached end whilst reading header");
        return Optional.empty();
      }
      length =
          ByteBuffer.wrap(read, 0, HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN).getShort()
              - HEADER_SIZE;

      // Read the body
      if (input.readNBytes(read, 0, length) != length) {
        logger.debug("Reached end whilst reading body");
        return Optional.empty();
      }
    } catch (SocketException e) {
      logger.debug("Server initiated disconnect");
      return Optional.empty();
    }

    // Decrypt
    cryptLock.lock();
    try {
      length = crypt.decrypt(read, 0, length);
    } catch (ShortBufferException e) {
      throw new RuntimeException(e);
    } finally {
      cryptLock.unlock();
    }

    // Remove padding
    while (length > 4
        && read[length - 1] == 0
        && read[length - 2] == 0
        && read[length - 3] == 0
        && read[length - 4] == 0) {
      length -= 4;
    }

    // Checksum
    var expected = AuthCryptUtil.calculateChecksum(read, 0, length - AuthCryptUtil.BLOCK_SIZE);
    var actual =
        ByteBuffer.wrap(read, length - AuthCryptUtil.BLOCK_SIZE, AuthCryptUtil.BLOCK_SIZE)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt();
    if (expected != actual) {
      logger.error("Invalid checksum expected={} actual={}", expected, actual);
      throw new RuntimeException("Invalid checksum");
    }

    // Decode the message
    var message = codec.decode(read, 0, length);
    logger.debug("Received {}", message);
    return Optional.of(message);
  }

  @Override
  public void close() {
    try {
      socket.close();
    } catch (IOException e) {
      logger.debug("Failed to close the connection", e);
    }
  }
}
