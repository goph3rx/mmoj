package com.github.goph3rx.game.messages;

/**
 * Response to indicate that the requested character could not be deleted.
 *
 * @param reason Reason for the failure.
 */
public record ServerCharDeleteFail(CharDeleteFailReason reason) {}
