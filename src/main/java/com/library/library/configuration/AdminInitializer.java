package com.library.library.configuration;

import com.library.library.model.enumerations.Gender;
import com.library.library.model.enumerations.Role;
import com.library.library.model.UserCredentials;
import com.library.library.model.Users;
import com.library.library.service.UserCredentialsService;
import com.library.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class AdminInitializer implements CommandLineRunner {

    private final UserCredentialsService userCredentialsService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.info.username:admin@example.com}")
    private String adminEmail;

    @Value("${admin.info.password:Admin@1234}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        try {
            if (!userCredentialsService.existsByRole(Role.ADMIN)) {
                if (adminPassword.length() < 8) {
                    throw new IllegalStateException("Admin password must be ≥8 characters");
                }

                UserCredentials adminCredentials = new UserCredentials();

                adminCredentials.setEmail(adminEmail);
                adminCredentials.setPassword(passwordEncoder.encode(adminPassword));
                adminCredentials.setRole(Role.ADMIN);
                adminCredentials.setEnabled(true);
                adminCredentials.setEmailVerified(true);

                Users adminUser = new Users();
                adminUser.setGender(Gender.MALE);
                adminUser.setDisplayName("admin");


                adminUser.setCredentials(adminCredentials);
                adminCredentials.setUser(adminUser);

                userService.save(adminUser);
                log.info("Admin user created successfully");
                log.info("\n\n\n\n\n {} \nn\n\n\\nn\n\n",adminCredentials.getPassword());

            } else {
                log.info("Admin user already exists");
            }
        } catch (Exception e) {
            log.error("Critical: Failed to initialize admin user", e);
            throw new RuntimeException("Admin initialization failed", e);
        }
    }


}