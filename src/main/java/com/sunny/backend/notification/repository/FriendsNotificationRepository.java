package com.sunny.backend.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.notification.domain.FriendsNotification;

public interface FriendsNotificationRepository extends JpaRepository<FriendsNotification, Long> {
	List<FriendsNotification> findByUsers_Id(Long userId);
}
