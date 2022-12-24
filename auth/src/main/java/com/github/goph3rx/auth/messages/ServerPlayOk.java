package com.github.goph3rx.auth.messages;

/**
 * Response that completes the world transfer.
 *
 * @param playToken Second part of the transfer token.
 */
public record ServerPlayOk(long playToken) {}
