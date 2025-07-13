package com.library.library.Service.Implements;

import com.library.library.Model.Enumerations.Role;
import com.library.library.Model.Dto.RegistrationDto;
import com.library.library.Model.UserCredentials;
import com.library.library.Model.Users;
import com.library.library.Repository.UserCredentialsRepository;
import com.library.library.Security.Interfaces.LoginAttemptTracker;
import com.library.library.Service.LibraryService;
import com.library.library.Service.UserCredentialsService;
import com.library.library.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
public class UserCredentialsServiceImp implements UserCredentialsService {

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private LoginAttemptTracker loginAttemptService;

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;


    @Override
    @Transactional(readOnly = true)
    public UserCredentials save(UserCredentials credentials) {
        userCredentialsRepository.save(credentials);
        return credentials;
    }

    @Override
    public boolean existsByEmail(String username) {
        return userCredentialsRepository.existsByEmail(username);
    }

    @Override
    public boolean existsByRole(Role role) {
        return userCredentialsRepository.existsByRole(role);
    }

    @Override
    public Optional<UserCredentials> findByEmail(String email) {
        return userCredentialsRepository.findByEmail(email);
    }


    public void loginFailed(String email) {
        loginAttemptService.loginFailed(email);
    }

    public void loginSucceeded(String email) {
        loginAttemptService.loginSucceeded(email);
    }

    public boolean isAccountLocked(String email) {
        return loginAttemptService.isAccountLocked(email);
    }

    public int getRemainingAttempts(String email) {
        return loginAttemptService.getRemainingAttempts(email);
    }

    public Instant getLockedUntil(String email) {
        return loginAttemptService.getLockedUntil(email)
                .orElseThrow(() -> new IllegalStateException("Account is not locked"));
    }

    @Transactional
    public UserCredentials create(RegistrationDto registerRequest) {
        // Validate password match
        if (!registerRequest.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords don't match");
        }

        // Check if email already exists
        if (existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Check if username already exists
        if (userService.existsByDisplayName(registerRequest.getName())) {
            throw new IllegalArgumentException("Username already in use");
        }

        // Create user credentials
        UserCredentials credentials = new UserCredentials();
        credentials.setEmail(registerRequest.getEmail());
        credentials.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        credentials.setRole(Role.USER);
        credentials.setEnabled(true);
        credentials.setEmailVerified(false);
        credentials.setLocked(false);

        // Create user
        Users user = new Users();
        user.setDisplayName(registerRequest.getName());

        user.setCredentials(credentials);
        credentials.setUser(user);

        // Save and update statistics
        userService.save(user);
        libraryService.incrementUsers();

        return userCredentialsRepository.save(credentials);
    }

}
