package com.sunny.backend.repository;

import com.sunny.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Notification findByUsers_Id (Long userId);
}
