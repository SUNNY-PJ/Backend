package com.sunny.backend.notification.repository;

import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionNotificationRepository extends JpaRepository<CompetitionNotification,Long> {
  List<CompetitionNotification> findByUsers_Id (Long userId);
}
