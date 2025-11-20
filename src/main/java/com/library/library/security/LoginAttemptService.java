package com.library.library.security;

import com.library.library.model.UserCredentials;
import com.library.library.repository.UserCredentialsRepository;
import com.library.library.security.interfaces.LoginAttemptTracker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.library.library.exception.infrastructure.AccountLockedException;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginAttemptService implements LoginAttemptTracker {
    public static final int MAX_ATTEMPTS = 5;
    public static final Duration LOCK_DURATION = Duration.ofMinutes(15);
    private final ConcurrentMap<String, Attempt> attemptsCache = new ConcurrentHashMap<>();

    @Autowired
    private UserCredentialsRepository userCredentialsService;

    @Getter
    @AllArgsConstructor
    private static class Attempt {
        private int attempts;
        private Instant lastAttempt;
    }

    @Transactional
    @Override
    public void loginFailed(String email) {
        Attempt attempt = attemptsCache.compute(email, (k, v) ->
                v == null ? new Attempt(1, Instant.now())
                        : new Attempt(v.attempts + 1, Instant.now()));

        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            Duration lockDuration = calculateLockDuration(attempt.getAttempts());
            Instant lockedUntil = Instant.now().plus(lockDuration);
            lockAccount(email, lockedUntil);
            throw new AccountLockedException("Account temporarily locked", lockedUntil);
        }
    }



    @Transactional
    public void loginSucceeded(String email) {
        attemptsCache.remove(email);
        unlockAccountIfLocked(email);
        log.info("Successful login for {}", email);
    }

    public boolean isAccountLocked(String email) {
        return userCredentialsService.findByEmail(email)
                .map(credentials ->
                        Boolean.TRUE.equals(credentials.getLocked()) ||
                                checkAccountBlockStatus(credentials))
                .orElse(false);
    }
    public boolean isAccountDeleted(String email) {
        return userCredentialsService.findByEmail(email)
                .map(UserCredentials::isAccountExpired)
                .orElse(false);
    }


    private boolean checkAccountBlockStatus(UserCredentials credentials) {
        // Check permanent lock first
        if (credentials.isPermanentlyLocked()) {
            return true;
        }

        // Check temporary in-memory lock
        Attempt attempt = attemptsCache.get(credentials.getEmail());
        return attempt != null &&
                attempt.getAttempts() >= MAX_ATTEMPTS &&
                !isLockExpired(attempt);
    }

    private boolean isLockExpired(Attempt attempt) {
        return attempt.getLastAttempt()
                .plus(calculateLockDuration(attempt.getAttempts()))
                .isBefore(Instant.now());
    }

    public int getRemainingAttempts(String email) {
        Attempt attempt = attemptsCache.get(email);
        return attempt != null ? MAX_ATTEMPTS - attempt.getAttempts() : MAX_ATTEMPTS;
    }


    // Helper Methods
    private void lockAccount(String email, Instant lockedUntil) {
        userCredentialsService.findByEmail(email).ifPresent(credentials -> {
            credentials.setLocked(true);
            credentials.setLockedAt(Instant.now());
            userCredentialsService.save(credentials);
            log.debug("Account {} locked until {}", email, lockedUntil);
        });
    }

    private void unlockAccountIfLocked(String email) {
        attemptsCache.remove(email);
        userCredentialsService.findByEmail(email).ifPresent(credentials -> {
            if (credentials.isPermanentlyLocked()) {
                credentials.setLocked(false);
                userCredentialsService.save(credentials);
                log.debug("Account {} unlocked", email);
            }
        });
    }

    //    5	15 * 2^(5-5) = 15*1	15 minutes
    //    6	15 * 2^(6-5) = 15*2	30 minutes
    //    7	15 * 2^(7-5) = 15*4	60 minutes
    //    8	15 * 2^(8-5) = 15*8	2 hours
    private Duration calculateLockDuration(int attempts) {
        if (attempts <= MAX_ATTEMPTS) return LOCK_DURATION;
        return LOCK_DURATION.multipliedBy((long) Math.pow(2, attempts - MAX_ATTEMPTS));
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void cleanStaleAttempts() {
        Instant cutoff = Instant.now().minus(LOCK_DURATION.multipliedBy(2));
        int initialSize = attemptsCache.size();
        attemptsCache.entrySet().removeIf(entry ->
                entry.getValue().getLastAttempt().isBefore(cutoff));
        log.debug("Cleaned {} stale login attempts", initialSize - attemptsCache.size());
    }

    public boolean shouldBypassAttemptCheck(String email, String provider) {
        return userCredentialsService.findByEmail(email)
                .map(credentials ->
                        credentials.hasProvider(provider) &&
                                credentials.isOAuth2User())
                .orElse(false);
    }
    // New method to get lock time
    public Optional<Instant> getLockedUntil(String email) {
        return userCredentialsService.findByEmail(email)
                .filter(UserCredentials::getLocked)
                .map(credentials -> {
                    Attempt attempt = attemptsCache.get(email);
                    if (attempt != null && attempt.getAttempts() >= MAX_ATTEMPTS) {
                        return attempt.getLastAttempt()
                                .plus(calculateLockDuration(attempt.getAttempts()));
                    }
                    return credentials.getLockedAt()
                            .plus(LOCK_DURATION);
                });
    }

}