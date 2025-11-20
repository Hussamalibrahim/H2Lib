package com.library.library.repository;

import com.library.library.model.enumerations.Role;
import com.library.library.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials,Long> {
    boolean existsByRole(Role role);

    Optional<UserCredentials> findByEmail(String username);

    boolean existsByEmail(String username);

    boolean existsByLockedAtAndEmail(Instant lockedAt, String email);

}
