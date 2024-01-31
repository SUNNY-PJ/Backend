package com.sunny.backend.friends.service;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sunny.backend.friends.dto.response.FriendCheckResponse;
import com.sunny.backend.friends.dto.response.FriendResponse;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.Status;
import com.sunny.backend.friends.dto.response.FriendStatusResponse;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
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

	public FriendStatusResponse getFriends(CustomUserPrincipal customUserPrincipal) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		List<FriendResponse> friendResponses = friendRepository.getFriendResponse(tokenUserId);
		return FriendStatusResponse.of(friendResponses, friendResponses);
	}

	public void isMyselfFriendApplication(Long id, Long userFriendId) {
		if(id.equals(userFriendId)) {
			throw new CustomException(FriendErrorCode.FRIEND_NOT_MYSELF);
		}
	}

	public void addFriend(CustomUserPrincipal customUserPrincipal, Long userFriendId) {
		Users user = customUserPrincipal.getUsers();
		Users userFriend = userRepository.getById(userFriendId);
		isMyselfFriendApplication(user.getId(), userFriendId);
		Friend friend = getByUserAndUserFriend(user, userFriend, Status.WAIT);

		String title = "[SUNNY] " + user.getName();
		String noticeTitle = "친구 신청을 받았어요.";
		String noticeBody = "[SUNNY] " + user.getName() + "님이 친구를 신청했어요!";
		sendNotifications(friend, title, noticeTitle, noticeBody);
	}

	@Transactional
	public void approveFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateFriendsByUser(friend.getUsers().getId(), tokenUserId);

		friend.approveStatus();
		getByUserAndUserFriend(friend.getUsers(), friend.getUserFriend(), Status.APPROVE);

		String title = "[SUNNY] " + friend.getUsers().getName();
		String noticeTitle = "친구 신청 결과를 알려드려요.";
		String noticeBody = "[SUNNY] " + friend.getUsers().getName() + "님이 친구 신청을 수락했어요";
		sendNotifications(friend, title, noticeTitle, noticeBody);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateFriendsByUser(friend.getUsers().getId(), tokenUserId);

		friendRepository.deleteById(friend.getId());

		String title = "[SUNNY] " + friend.getUsers().getName();
		String noticeTitle = "친구 신청 결과를 알려드려요.";
		String noticeBody = "[SUNNY] " + friend.getUsers().getName() + "님이 친구 신청을 거절했어요";
		sendNotifications(friend, title, noticeTitle, noticeBody);
	}

	public void sendNotifications(Friend friend, String title, String noticeTitle, String noticeBody) {
		Long postAuthor=friend.getUsers().getId();
		FriendsNotification friendsNotification=FriendsNotification.builder()
				.users(friend.getUserFriend()) //상대방꺼
				.friend(friend.getUsers())
				.title(noticeTitle)
				.body(noticeBody)
				.createdAt(LocalDateTime.now())
				.build();
		friendsNotificationRepository.save(friendsNotification);
		List<Notification> notificationList=notificationRepository.findByUsers_Id(postAuthor);

		if(notificationList.size()!=0) {
			NotificationPushRequest notificationPushRequest =
				new NotificationPushRequest(postAuthor, noticeTitle, noticeBody);
			notificationService.sendNotificationToFriends(title,notificationPushRequest);
		}
	}

	public Friend getByUserAndUserFriend(Users user, Users userFriend, Status status) {
		Optional<Friend> optionalFriend = friendRepository
			.findByUsers_IdAndUserFriend_Id(userFriend.getId(), user.getId());

		if(optionalFriend.isPresent()) {
			Friend friend = optionalFriend.get();
			friend.validateStatus();
		}

		Friend friends = Friend.builder()
			.users(userFriend)
			.userFriend(user)
			.status(status)
			.build();
		return friendRepository.save(friends);
	}

	@Transactional
	public void deleteFriends(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		friend.validateFriendsByUser(friend.getUsers().getId(), customUserPrincipal.getUsers().getId());

		Optional<Friend> optionalFriend = friendRepository
			.findByUsers_IdAndUserFriend_Id(friend.getUserFriend().getId(), friend.getUsers().getId());
		optionalFriend.ifPresent(value -> friendRepository.deleteById(value.getId()));

		friendRepository.deleteById(friendId);
	}

	public FriendCheckResponse checkFriend(CustomUserPrincipal customUserPrincipal, Long userFriendId) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		Optional<Friend> friendsOptional = friendRepository.findByUsers_IdAndUserFriend_Id(userFriendId, tokenUserId);

		boolean isFriend = false;
		Status status = null;

		if(friendsOptional.isPresent()) {
			Friend friend = friendsOptional.get();
			status = friend.getStatus();
			isFriend = friend.isApproveStatus();
		}

		return new FriendCheckResponse(isFriend, status);
	}
}