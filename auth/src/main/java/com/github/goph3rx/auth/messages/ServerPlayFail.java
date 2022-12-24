package com.github.goph3rx.auth.messages;

/**
 * Response to indicate a failed transfer.
 *
 * @param reason Reason for the failure.
 */
public record ServerPlayFail(LoginFailReason reason) {}
