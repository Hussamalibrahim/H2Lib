package com.library.library.Repository;

import com.library.library.Model.Enumerations.Role;
import com.library.library.Model.UserCredentials;
import org.springframework.data.geo.GeoResult;
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
