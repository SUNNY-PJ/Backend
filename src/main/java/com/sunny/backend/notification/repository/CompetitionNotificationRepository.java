package com.sunny.backend.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.user.domain.Users;

public interface CompetitionNotificationRepository extends JpaRepository<CompetitionNotification, Long> {
	List<CompetitionNotification> findByUsers_Id(Long userId);

	void deleteByUsers(Users users);
}
