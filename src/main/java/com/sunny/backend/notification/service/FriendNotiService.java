package com.sunny.backend.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendNotiService {
	private final FriendsNotificationRepository friendsNotificationRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationService notificationService;

	public void sendNotifications(String title, String body, String bodyTitle, Friend friend) {
		Long postAuthor = friend.getUsers().getId();
		FriendsNotification friendsNotification = FriendsNotification.builder()
			.friend(friend)
			.title(bodyTitle)
			.body(body)
			.build();
		friendsNotificationRepository.save(friendsNotification);
		List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);
		if (notificationList.size() != 0) {
			String notificationBody = friend.getUserFriend().getNickname() + body;
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				notificationBody,
				bodyTitle
			);
			notificationService.sendNotificationToFriends(title, notificationPushRequest);
		}
	}
}