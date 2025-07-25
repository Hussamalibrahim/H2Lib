package com.library.library.Repository;

import com.library.library.Model.Notification;
import com.library.library.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query(value = "SELECT * FROM notification WHERE user_id = :userId", nativeQuery = true)
    Optional<Notification> findByUserNative(@Param("userId") Long userId);
}
