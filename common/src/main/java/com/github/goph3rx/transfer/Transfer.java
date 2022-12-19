package com.github.goph3rx.transfer;

import java.time.LocalDateTime;

/**
 * Transfer record.
 *
 * @param account Account name.
 * @param auth First part of the token.
 * @param play Second part of the token.
 * @param expiry When the record expires.
 */
public record Transfer(String account, long auth, long play, LocalDateTime expiry) {}
