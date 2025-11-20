package com.library.library.repository;

import com.library.library.model.enumerations.Role;
import com.library.library.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    //    long countByNotifications(Notifications notifications);
    @Query("select (count(u) > 0) from Users u where u.displayName = ?1")
    boolean existsByDisplayName(String displayName);

    // UsersRepository.java
    @Query("SELECT COUNT(u) > 0 FROM Users u JOIN u.credentials c WHERE u.usernameKey = :usernameKey AND c.role = :role")
    boolean existsByUsernameKeyAndRole(@Param("usernameKey") String usernameKey, @Param("role") Role role);

}
