package com.github.goph3rx.game.messages;

/**
 * Request to choose the protocol for server communication.
 *
 * @param version Version supported by the client.
 */
public record ClientProtocolVersion(int version) {}
