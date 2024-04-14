package com.sunny.backend.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.CompetitionNotificationRepository;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendNotiService {
	private final FriendsNotificationRepository friendsNotificationRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationService notificationService;
	private final CompetitionNotificationRepository competitionNotificationRepository;

	public void sendNotifications(String title, String body, String bodyTitle, Friend friend) {
		Long postAuthor = friend.getUserFriend().getId();
		FriendsNotification friendsNotification = FriendsNotification.builder()
			.users(friend.getUserFriend())
			.friend(friend.getUsers())
			.title(bodyTitle)
			.body(body)
			.build();
		friendsNotificationRepository.save(friendsNotification);
		List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);
		if (notificationList.size() != 0) {
			String notificationBody = friend.getUsers().getNickname() + body;
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				notificationBody,
				bodyTitle
			);
			notificationService.sendNotificationToFriends(title, notificationPushRequest);
		}
	}

	public void sendCompetitionNotifications(String title, String body, String bodyTitle, Users users, Users friend,
		FriendCompetition friendCompetition) {
		Long postAuthor = users.getId();
		CompetitionNotification competitionNotification = CompetitionNotification.builder()
			.users(users)
			.friend(friend)
			.friendCompetition(friendCompetition)
			.title(bodyTitle)
			.body(body)
			.build();
		competitionNotificationRepository.save(competitionNotification);
		List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);
		if (notificationList.size() != 0) {
			String notificationBody = friend.getNickname() + body;
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				notificationBody,
				bodyTitle
			);
			notificationService.sendNotificationToFriends(title, notificationPushRequest);
		}
	}
}