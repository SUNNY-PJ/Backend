package com.sunny.backend.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.notification.domain.UserReportNotification;
import com.sunny.backend.user.domain.Users;

public interface UserReportNotificationRepository extends JpaRepository<UserReportNotification, Long> {
	List<UserReportNotification> findByUsers_Id(Long userId);

	void deleteByUsersOrWarnUser(Users users, Users warnUsrs);
}
