package com.github.goph3rx.game.messages;

/**
 * Response to indicate a failure during character creation.
 *
 * @param reason Reason for the failure.
 */
public record ServerCharCreateFail(CharCreateFailReason reason) {}
