package com.sunny.backend.repository;

import com.sunny.backend.entity.Notification;
import com.sunny.backend.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
