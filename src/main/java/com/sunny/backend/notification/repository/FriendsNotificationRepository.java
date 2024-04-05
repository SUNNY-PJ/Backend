package com.sunny.backend.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.user.domain.Users;

public interface FriendsNotificationRepository extends JpaRepository<FriendsNotification, Long> {
	List<FriendsNotification> findByUsers_Id(Long userId);

	void deleteByUsersOrFriend(Users users, Users friend);
}
