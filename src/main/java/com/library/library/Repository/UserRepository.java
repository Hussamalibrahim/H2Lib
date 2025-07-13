package com.library.library.Repository;

import com.library.library.Model.Enumerations.Role;
import com.library.library.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    //    long countByNotifications(Notifications notifications);
    boolean existsByDisplayName(String displayName);

    // UsersRepository.java
    @Query("SELECT COUNT(u) > 0 FROM Users u JOIN u.credentials c WHERE u.usernameKey = :usernameKey AND c.role = :role")
    boolean existsByUsernameKeyAndRole(@Param("usernameKey") String usernameKey, @Param("role") Role role);

}
