package com.github.goph3rx.auth.messages;

/**
 * Response to indicate that the account is banned.
 *
 * @param reason Reason for the ban.
 */
public record ServerAccountKicked(AccountKickedReason reason) {}
