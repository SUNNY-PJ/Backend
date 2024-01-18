package com.sunny.backend.notification.repository;

import com.sunny.backend.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUsers_Id (Long userId);
}
