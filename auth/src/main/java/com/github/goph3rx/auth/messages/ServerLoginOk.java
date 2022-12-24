package com.github.goph3rx.auth.messages;

/**
 * Response to indicate a successful login.
 *
 * @param authToken First part of the transfer token.
 */
public record ServerLoginOk(long authToken) {}
