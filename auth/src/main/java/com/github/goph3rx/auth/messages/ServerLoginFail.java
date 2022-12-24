package com.github.goph3rx.auth.messages;

/**
 * Response to indicate a failed login.
 *
 * @param reason Reason for the failure.
 */
public record ServerLoginFail(LoginFailReason reason) {}
