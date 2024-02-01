package com.sunny.backend.notification.repository;

import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.FriendsNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendsNotificationRepository extends JpaRepository<FriendsNotification,Long> {
  List<FriendsNotification> findByFriend_Id(Long userId);
}
