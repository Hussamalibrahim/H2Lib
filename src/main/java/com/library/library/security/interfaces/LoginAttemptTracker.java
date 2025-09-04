package com.library.library.security.interfaces;

import java.time.Instant;
import java.util.Optional;

public interface LoginAttemptTracker {
    void loginFailed(String email);

    void loginSucceeded(String email);

    Optional<Instant> getLockedUntil(String email);

    boolean isAccountLocked(String email);

    int getRemainingAttempts(String email);

    boolean isAccountDeleted(String email);
}
