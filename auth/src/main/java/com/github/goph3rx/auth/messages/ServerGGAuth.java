package com.github.goph3rx.auth.messages;

/**
 * Response to indicate the outcome of GG auth.
 *
 * @param result Result.
 */
public record ServerGGAuth(GGAuthResult result) {}
