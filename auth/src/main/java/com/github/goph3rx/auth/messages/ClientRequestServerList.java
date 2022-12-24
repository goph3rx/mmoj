package com.github.goph3rx.auth.messages;

/**
 * Request to display the list of worlds.
 *
 * @param authToken First part of the transfer token.
 */
public record ClientRequestServerList(long authToken) {}
