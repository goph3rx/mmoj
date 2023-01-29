package com.github.goph3rx.game.messages;

/**
 * Response to indicate a failed login.
 *
 * @param reason Reason for the failure.
 */
public record ServerAuthLoginFail(AuthLoginFailReason reason) {}
