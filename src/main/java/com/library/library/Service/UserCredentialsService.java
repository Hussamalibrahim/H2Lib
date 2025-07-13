package com.library.library.Service;

import com.library.library.Model.Enumerations.Role;
import com.library.library.Model.Dto.RegistrationDto;
import com.library.library.Model.UserCredentials;

import java.time.Instant;
import java.util.Optional;

public interface UserCredentialsService {

    // Will be user in security layer no need to make it dto


    UserCredentials save(UserCredentials credentials);

    boolean existsByEmail(String username);

    boolean existsByRole(Role role);

    void loginFailed(String email);

    void loginSucceeded(String email);

    Optional<UserCredentials> findByEmail(String email);

    UserCredentials create(RegistrationDto registerRequest);

    int getRemainingAttempts(String email);

    boolean isAccountLocked(String email);

    Instant getLockedUntil(String loginEmail);
}
