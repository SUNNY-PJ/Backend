package com.sunny.backend.friends.service;

import static com.sunny.backend.friends.exception.FriendErrorCode.*;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.dto.response.FriendListResponse;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.service.FriendNotiService;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {

	private final FriendRepository friendRepository;
	private final UserRepository userRepository;
	private final FriendNotiService friendNotiService;

	public FriendListResponse getFriends(CustomUserPrincipal customUserPrincipal) {
		Users users = customUserPrincipal.getUsers();
		List<Friend> friends = friendRepository.findByUsers(users);
		return FriendListResponse.of(friends);
	}

	public void addFriend(CustomUserPrincipal customUserPrincipal, Long userFriendId) throws IOException {
		Users user = customUserPrincipal.getUsers();
		user.canNotMySelf(userFriendId);
		Users userFriend = userRepository.getById(userFriendId);

		friendRepository.findByUsersAndUserFriend(user, userFriend)
			.ifPresent(friend -> {
				if (friend.isEqualToFriendStatus(FriendStatus.FRIEND)) {
					throw new CustomException(ALREADY_FRIEND);
				} else if (friend.isEqualToFriendStatus(FriendStatus.SEND)) {
					throw new CustomException(FRIEND_SEND);
				} else if (friend.isEqualToFriendStatus(FriendStatus.RECEIVE)) {
					throw new CustomException(FRIEND_RECEIVE);
				}
				throw new CustomException(FRIEND_SERVER_ERROR);
			});

		Friend sendFriend = Friend.of(user, userFriend, FriendStatus.SEND);
		Friend receiveFriend = Friend.of(userFriend, user, FriendStatus.RECEIVE);

		friendRepository.save(sendFriend);
		friendRepository.save(receiveFriend);

		String title = "[SUNNY] " + sendFriend.getUsers().getNickname();
		String body = "님이 친구를 신청했어요!";
		String bodyTitle = "친구 신청을 받았어요";
		friendNotiService.sendNotifications(title, body, bodyTitle, sendFriend);
	}

	@Transactional
	public void approveFriend(CustomUserPrincipal customUserPrincipal, Long friendId)
		throws IOException {
		Friend receiveFriend = friendRepository.getById(friendId);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		receiveFriend.validateUser(tokenUserId);

		// 상대 친구 관계를 조회, 승인하는 경우
		// 친구 관계가 존재하는 경우 SEND -> FRIEND
		// 친구 관계가 존재하지 않는 경우는 서버 문제임으로 받은 친구 관계 삭제 후 에러 처리
		friendRepository.findByUsersAndUserFriend(receiveFriend.getUserFriend(), receiveFriend.getUsers())
			.ifPresentOrElse(
				sendFriend -> sendFriend.updateFriendStatus(FriendStatus.FRIEND),
				() -> {
					friendRepository.delete(receiveFriend);
					throw new CustomException(FRIEND_SERVER_ERROR);
				}
			);

		String title = "[SUNNY] " + receiveFriend.getUsers().getNickname();
		String body = "님이 친구 신청을 수락했어요";
		String bodyTitle = "친구 신청 결과를 알려드려요";
		friendNotiService.sendNotifications(title, body, bodyTitle, receiveFriend);
		receiveFriend.updateFriendStatus(FriendStatus.FRIEND);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId)
		throws IOException {
		Friend receiveFriend = friendRepository.getById(friendId);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		receiveFriend.validateUser(tokenUserId);

		// 상대 친구 관계를 조회, 거절하는 경우
		// 친구 관계가 존재하는 경우 상대 친구 관계도 같이 삭제
		// 친구 관계가 존재하지 않는 경우는 서버 문제임으로 받은 친구 관계 삭제 후 에러 처리
		friendRepository.findByUsersAndUserFriend(receiveFriend.getUserFriend(), receiveFriend.getUsers())
			.ifPresentOrElse(
				friendRepository::delete,
				() -> {
					friendRepository.delete(receiveFriend);
					throw new CustomException(FRIEND_SERVER_ERROR);
				}
			);
		String title = "[SUNNY] " + receiveFriend.getUsers().getNickname();
		String body = "님이 친구 신청을 거절했어요";
		String bodyTitle = "친구 신청 결과를 알려드려요";
		friendNotiService.sendNotifications(title, body, bodyTitle, receiveFriend);

		friendRepository.delete(receiveFriend);
	}

	@Transactional
	public void deleteFriends(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(customUserPrincipal.getUsers().getId());
		if (friend.hasCompetition()) {
			friendRepository.updateCompetitionToNull(friend.getCompetition().getId());
		}
		// 상대편 친구 관계 삭제
		friendRepository.findByUsersAndUserFriend(friend.getUserFriend(), friend.getUsers())
			.ifPresent(friendRepository::delete);
		// 나의 친구 관계 삭제
		friendRepository.delete(friend);
	}

}
