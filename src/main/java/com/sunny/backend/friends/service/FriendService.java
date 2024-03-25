package com.sunny.backend.friends.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.dto.response.FriendListResponse;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {

	private final FriendRepository friendRepository;
	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationService notificationService;
	private final FriendsNotificationRepository friendsNotificationRepository;

	public FriendListResponse getFriends(CustomUserPrincipal customUserPrincipal) {
		Users users = customUserPrincipal.getUsers();
		List<Friend> friendResponses = friendRepository.findByUsers(users);
		return FriendListResponse.of(friendResponses);
	}

	public void addFriend(CustomUserPrincipal customUserPrincipal, Long userFriendId)
		throws IOException {
		Users user = customUserPrincipal.getUsers();
		user.canNotMySelf(userFriendId);
		Users userFriend = userRepository.getById(userFriendId);
		//title,bodyTitle,body 따로 전달
		getByUserAndUserFriend(user, userFriend, FriendStatus.PENDING);
	}

	private void sendNotifications(Users users, Friend friend) throws IOException {
		if (users != null && friend != null && friend.getUsers() != null && friend.getUserFriend() != null) {
			Long postAuthor = friend.getUsers().getId();
			String body = friend.getUserFriend().getNickname() + "님이 친구 신청을 거절했어요";
			String title = "[SUNNY] " + users.getNickname();
			String bodyTitle = "친구 신청 결과를 알려드려요";
			if (FriendStatus.PENDING.equals(friend.getStatus())) {
				bodyTitle = "친구 신청을 받았어요.";
				body = users.getNickname() + "님이 친구를 신청했어요!";
			}
			if (FriendStatus.FRIEND.equals(friend.getStatus())) {
				body = friend.getUserFriend().getNickname() + "님이 친구 신청을 수락했어요";
			}
			FriendsNotification friendsNotification = FriendsNotification.builder()
				.users(friend.getUserFriend()) // 상대방꺼
				.friend(friend.getUsers())
				.title(bodyTitle)
				.body(body)
				.createdAt(LocalDateTime.now())
				.build();
			friendsNotificationRepository.save(friendsNotification);
			List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);
			if (notificationList.size() != 0) {
				NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
					postAuthor,
					bodyTitle,
					body
				);
				notificationService.sendNotificationToFriends(title, notificationPushRequest);
			}
		} else {
			throw new IOException("유저나 친구가 존재하지 않습니다.");
		}
	}

	@Transactional
	public void approveFriend(CustomUserPrincipal customUserPrincipal, Long friendId)
		throws IOException {
		Friend friend = friendRepository.getById(friendId);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateUser(tokenUserId);
		friend.approveStatus();
		getByUserAndUserFriend(friend.getUsers(), friend.getUserFriend(), FriendStatus.FRIEND);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateUser(tokenUserId);
		friendRepository.deleteById(friend.getId());
	}

	public void getByUserAndUserFriend(Users user, Users userFriend, FriendStatus friendStatus)
		throws IOException {
		Optional<Friend> optionalFriend = friendRepository.findByUsersAndUserFriend(userFriend, user);

		if (optionalFriend.isEmpty()) {
			Friend saveUserFriend = Friend.of(userFriend, user, friendStatus);
			friendRepository.save(saveUserFriend);
			sendNotifications(user, saveUserFriend);
		} else {
			Friend friend = optionalFriend.get();
			friend.validateProposal();
			sendNotifications(user, friend);
		}
	}

	@Transactional
	public void deleteFriends(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(customUserPrincipal.getUsers().getId());
		friendRepository.delete(friend);
	}

}
