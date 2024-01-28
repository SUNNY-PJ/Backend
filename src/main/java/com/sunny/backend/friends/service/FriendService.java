package com.sunny.backend.friends.service;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import java.io.IOException;
import java.time.LocalDate;
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

	public void addFriend(CustomUserPrincipal customUserPrincipal, Long userFriendId)
			throws IOException {
		Users user = customUserPrincipal.getUsers();
		Users userFriend = userRepository.getById(userFriendId);
		getByUserAndUserFriend(user, userFriend, Status.WAIT);
	}

	private void sendNotifications(Users users, Friend friend) throws IOException {
		Long postAuthor=friend.getUsers().getId();
		String body =friend.getUserFriend().getName()+"님이 친구 신청을 거절했어요";;
		String title="[SUNNY] "+users.getName();
		String bodyTitle="친구 신청 결과를 알려드려요";
		System.out.println(friend.getStatus());
		if(Status.WAIT.equals(friend.getStatus())) {
			bodyTitle="친구 신청을 받았어요.";
			body = users.getName()+"님이 친구를 신청했어요!";

		}
		if(Status.APPROVE.equals(friend.getStatus())) {
			body = friend.getUserFriend().getName()+"님이 친구 신청을 수락했어요";

		}
		FriendsNotification friendsNotification=FriendsNotification.builder()
				.users(friend.getUserFriend()) //상대방꺼
				.friend(friend.getUsers())
				.title(bodyTitle)
				.body(body)
				.createdAt(LocalDateTime.now())
				.build();
		friendsNotificationRepository.save(friendsNotification);
		List<Notification> notificationList=notificationRepository.findByUsers_Id(postAuthor);
		System.out.println(notificationList.size());

		if(notificationList.size()!=0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
					postAuthor,
					bodyTitle,
					body
			);
			System.out.println(notificationPushRequest.getPostAuthor());
				notificationService.sendNotificationToFriends(title,notificationPushRequest);
			}
	}
	@Transactional
	public void approveFriend(CustomUserPrincipal customUserPrincipal, Long friendId)
			throws IOException {
		Friend friend = friendRepository.getById(friendId);
		System.out.println("friends_id");
		System.out.println(friend);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateFriendsByUser(friend.getUsers().getId(), tokenUserId);

		friend.approveStatus();
		getByUserAndUserFriend(friend.getUsers(), friend.getUserFriend(), Status.APPROVE);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateFriendsByUser(friend.getUsers().getId(), tokenUserId);

		friendRepository.deleteById(friend.getId());
	}

	public void getByUserAndUserFriend(Users user, Users userFriend, Status status)
			throws IOException {
		Optional<Friend> optionalFriend = friendRepository
			.findByUsers_IdAndUserFriend_Id(userFriend.getId(), user.getId());

		if(optionalFriend.isEmpty()) {
			Friend friends = Friend.builder()
				.users(userFriend)
				.userFriend(user)
				.status(status)
				.build();
			friendRepository.save(friends);
			sendNotifications(user,friends);
		} else {
			Friend friend = optionalFriend.get();
			friend.validateStatus();
			System.out.println("호출");
			sendNotifications(user,friend);
		}
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