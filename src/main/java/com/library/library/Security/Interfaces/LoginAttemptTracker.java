package com.library.library.Security.Interfaces;

import java.time.Instant;
import java.util.Optional;

public interface LoginAttemptTracker {
    void loginFailed(String email);

    void loginSucceeded(String email);

    Optional<Instant> getLockedUntil(String email);

    boolean isAccountLocked(String email);

    int getRemainingAttempts(String email);

}
