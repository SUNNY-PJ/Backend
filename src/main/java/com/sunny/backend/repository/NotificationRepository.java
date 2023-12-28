package com.sunny.backend.repository;

import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Notification;
import com.sunny.backend.entity.Scrap;
import com.sunny.backend.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Notification findByUsers_Id (Long userId);
}
