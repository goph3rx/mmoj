package com.github.goph3rx.auth.messages;

/**
 * Request to enter the specified world.
 *
 * @param authToken First part of the transfer token.
 * @param worldId Identifier of the chosen world.
 */
public record ClientRequestServerLogin(long authToken, int worldId) {}
