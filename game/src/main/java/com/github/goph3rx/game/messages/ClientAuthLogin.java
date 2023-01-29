package com.github.goph3rx.game.messages;

/**
 * Request to authenticate by transferring from the auth server.
 *
 * @param account Account name.
 * @param auth First part of the token.
 * @param play Second part of the token.
 */
public record ClientAuthLogin(String account, long auth, long play) {}
