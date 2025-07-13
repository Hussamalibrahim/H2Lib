package com.library.library.Exception.infrastructure;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
public class AccountLockedException extends AuthenticationException {
  private final Instant lockedUntil;
  private final long remainingMinutes;

  public AccountLockedException(String msg) {
    this(msg, Instant.now().plus(30, ChronoUnit.MINUTES)); // Default 30 minute lock
  }

  public AccountLockedException(String msg, Instant lockedUntil) {
    super(msg);
    this.lockedUntil = lockedUntil;
    this.remainingMinutes = ChronoUnit.MINUTES.between(Instant.now(), lockedUntil);
  }
}