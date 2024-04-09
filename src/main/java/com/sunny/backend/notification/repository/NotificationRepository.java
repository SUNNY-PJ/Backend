package com.sunny.backend.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.user.domain.Users;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUsers_Id(Long userId);

	void deleteByUsers(Users users);
}
