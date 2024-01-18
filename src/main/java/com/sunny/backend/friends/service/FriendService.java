package com.sunny.backend.friends.service;

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
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {
	private final FriendRepository friendRepository;
	private final UserRepository userRepository;

	public FriendStatusResponse getFriends(CustomUserPrincipal customUserPrincipal) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		List<FriendResponse> friendResponses = friendRepository.getFriendResponse(tokenUserId);
		return FriendStatusResponse.of(friendResponses, friendResponses);
	}

	public void addFriend(CustomUserPrincipal customUserPrincipal, Long userFriendId) {
		Users user = customUserPrincipal.getUsers();
		Users userFriend = userRepository.getById(userFriendId);

		getByUserAndUserFriend(user, userFriend, Status.WAIT);
	}

	@Transactional
	public void approveFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
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

	public void getByUserAndUserFriend(Users user, Users userFriend, Status status) {
		Optional<Friend> optionalFriend = friendRepository
			.findByUsers_IdAndUserFriend_Id(userFriend.getId(), user.getId());

		if(optionalFriend.isEmpty()) {
			Friend friends = Friend.builder()
				.users(userFriend)
				.userFriend(user)
				.status(status)
				.build();
			friendRepository.save(friends);
		} else {
			Friend friend = optionalFriend.get();
			friend.validateStatus();
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