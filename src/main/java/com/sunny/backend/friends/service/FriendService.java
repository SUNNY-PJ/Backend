package com.sunny.backend.friends.service;

import static com.sunny.backend.friends.exception.FriendErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.dto.response.FriendCompetitionDto;
import com.sunny.backend.friends.dto.response.FriendCompetitionQuery;
import com.sunny.backend.friends.dto.response.FriendCompetitionResponse;
import com.sunny.backend.friends.dto.response.FriendListResponse;
import com.sunny.backend.friends.dto.response.FriendResponse;
import com.sunny.backend.friends.repository.FriendCompetitionRepository;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.domain.NotifiacationSubType;
import com.sunny.backend.notification.repository.CompetitionNotificationRepository;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.service.FriendNotiService;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {

	private final FriendRepository friendRepository;
	private final FriendCompetitionRepository friendCompetitionRepository;
	private final UserRepository userRepository;
	private final FriendNotiService friendNotiService;
	private final CompetitionNotificationRepository competitionNotificationRepository;
	private final CompetitionRepository competitionRepository;
	private final FriendsNotificationRepository friendsNotificationRepository;

	public FriendListResponse getFriends(CustomUserPrincipal customUserPrincipal) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		// List<Long> id = user.getFriends()
		// 	.stream()
		// 	.map(friend -> friend.getUsers().getId())
		// 	.toList();
		// for (Friend friend : user.getFriends()) {
		// 	friendCompetitionRepository.findFirstByFriendAndFriendCompetitionStatusOrderByCreatedDateDesc()
		// }
		//
		//
		// List<FriendCompetitionDto> friendCompetitions = friendCompetitionRepository.getByFriendLeftJoinFriend(user.getId());
		// List<FriendCompetitionResponse> competitions = new ArrayList<>();
		// List<FriendCompetitionResponse> approveList = new ArrayList<>();
		// List<FriendResponse> waitList = new ArrayList<>();
		// for (FriendCompetitionDto friendCompetition : friendCompetitions) {
		// 	if (friendCompetition.getFriendStatus() == FriendStatus.FRIEND) {
		// 		if (friendCompetition.getFriendCompetitionStatus() == FriendCompetitionStatus.PROCEEDING) {
		// 			competitions.add(FriendCompetitionResponse.from(friendCompetition));
		// 		} else if(friendCompetition.getCompetitionOutputStatus() == CompetitionOutputStatus.NONE) {
		// 			approveList.add(FriendCompetitionResponse.from(friendCompetition));
		// 		}
		// 	} else if(friendCompetition.getFriendStatus() == FriendStatus.RECEIVE) {
		// 		waitList.add(FriendResponse.from(friendCompetition));
		// 	}
		// }

		List<FriendCompetitionResponse> competitions = friendCompetitionRepository.getFriendCompetitionProceeding(user.getId())
			.stream()
			.map(FriendCompetitionResponse::from)
			.toList();
		List<FriendCompetitionResponse> approveList = friendCompetitionRepository.getFriendCompetitionFriend(user.getId())
			.stream()
			.map(FriendCompetitionResponse::from)
			.toList();
		List<FriendResponse> waitList = friendRepository.findByUsersAndStatus(user, FriendStatus.RECEIVE)
			.stream()
			.map(FriendResponse::from)
			.toList();
		return new FriendListResponse(competitions, approveList, waitList);
	}

	@Transactional
	public void addFriend(CustomUserPrincipal customUserPrincipal, Long userFriendId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
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
		NotifiacationSubType subType = NotifiacationSubType.APPLY;
		friendNotiService.sendNotifications(title, body, bodyTitle, receiveFriend, subType);
	}

	@Transactional
	public void approveFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend receiveFriend = friendRepository.getById(friendId);
		receiveFriend.validateUser(customUserPrincipal.getId());

		// 상대 친구 관계를 조회, 승인하는 경우
		// 친구 관계가 존재하는 경우 SEND -> FRIEND
		// 친구 관계가 존재하지 않는 경우는 서버 문제임으로 받은 친구 관계 삭제 후 에러 처리
		Optional<Friend> friendOptional = friendRepository.findByUsersAndUserFriend(receiveFriend.getUserFriend(),
			receiveFriend.getUsers());
		if (friendOptional.isEmpty()) {
			friendRepository.delete(receiveFriend);
			throw new CustomException(FRIEND_SERVER_ERROR);
		}

		Friend sendFriend = friendOptional.get();
		sendFriend.updateFriendStatus(FriendStatus.FRIEND);

		String title = "[SUNNY] " + receiveFriend.getUsers().getNickname();
		String body = "님이 친구 신청을 수락했어요";
		String bodyTitle = "친구 신청 결과를 알려드려요";
		NotifiacationSubType subType = NotifiacationSubType.APPROVE;
		friendNotiService.sendNotifications(title, body, bodyTitle, sendFriend, subType);
		receiveFriend.updateFriendStatus(FriendStatus.FRIEND);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend receiveFriend = friendRepository.getById(friendId);
		receiveFriend.validateUser(customUserPrincipal.getId());

		// 상대 친구 관계를 조회, 거절하는 경우
		// 친구 관계가 존재하는 경우 상대 친구 관계도 같이 삭제
		// 친구 관계가 존재하지 않는 경우는 서버 문제임으로 받은 친구 관계 삭제 후 에러 처리
		Optional<Friend> friendOptional = friendRepository.findByUsersAndUserFriend(receiveFriend.getUserFriend(),
			receiveFriend.getUsers());
		if (friendOptional.isEmpty()) {
			friendRepository.delete(receiveFriend);
			throw new CustomException(FRIEND_SERVER_ERROR);
		}
		Friend sendFriend = friendOptional.get();

		String title = "[SUNNY] " + receiveFriend.getUsers().getNickname();
		String body = "님이 친구 신청을 거절했어요";
		String bodyTitle = "친구 신청 결과를 알려드려요";
		NotifiacationSubType subType = NotifiacationSubType.REFUSE;
		friendNotiService.sendNotifications(title, body, bodyTitle, sendFriend, subType);

		friendRepository.delete(friendOptional.get());
		friendRepository.delete(receiveFriend);
	}

	@Transactional
	public void deleteFriends(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		Users users = friend.getUsers();
		friend.validateUser(customUserPrincipal.getId());

		// List<FriendCompetition> friendCompetitions = friendCompetitionRepository.getByUserOrUserFriend(users.getId());
		// for (FriendCompetition friendCompetition : friendCompetitions) {
		// 	competitionNotificationRepository.deleteAllByFriendCompetition(friendCompetition);
		// 	friendCompetitionRepository.deleteById(friendCompetition.getId());
		// }
		// Set<Long> competitionIds = friendCompetitions.stream()
		// 	.map(friendCompetition -> friendCompetition.getCompetition().getId())
		// 	.collect(Collectors.toSet());
		// if (!competitionIds.isEmpty()) {
		// 	competitionRepository.deleteAllById(competitionIds);
		// }
		//
		// friendsNotificationRepository.deleteByUsersOrFriend(users, users);
		// friendRepository.deleteByUsersOrUserFriend(users, users);
	}

}