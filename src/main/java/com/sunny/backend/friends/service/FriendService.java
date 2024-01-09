package com.sunny.backend.friends.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.sunny.backend.dto.response.FriendsCheckResponse;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;
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

	public List<FriendsResponse> getFriends(
		CustomUserPrincipal customUserPrincipal, @Valid FriendStatus friendStatus) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();

		return friendRepository.findByUsers_IdAndStatus(tokenUserId, friendStatus)
				.stream()
				.map(FriendsResponse::from)
				.toList();
	}

	public void addFriend(CustomUserPrincipal customUserPrincipal, Long friendsId) {
		Users user = customUserPrincipal.getUsers();
		Users userFriend = userRepository.getById(friendsId);

		getByUserAndUserFriend(user, userFriend, FriendStatus.WAIT);
	}

	@Transactional
	public void approveFriend(CustomUserPrincipal customUserPrincipal, Long friendsSn) {
		Friend friend = friendRepository.getById(friendsSn);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateFriendsByUser(friend.getUsers().getId(), tokenUserId);

		friend.approveStatus();
		getByUserAndUserFriend(friend.getUsers(), friend.getUserFriend(), FriendStatus.APPROVE);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendsSn) {
		Friend friend = friendRepository.getById(friendsSn);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateFriendsByUser(friend.getUsers().getId(), tokenUserId);

		friendRepository.deleteById(friend.getId());
	}

	private void getByUserAndUserFriend(Users user, Users userFriend, FriendStatus status) {
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
	public void deleteFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsSn) {
		Friend friend = friendRepository.getById(friendsSn);
		friend.validateFriendsByUser(friend.getUsers().getId(), customUserPrincipal.getUsers().getId());

		friendRepository.deleteById(friendsSn);
	}

	public FriendsCheckResponse checkFriend(
		CustomUserPrincipal customUserPrincipal, Long friendsId) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		Optional<Friend> friendsOptional = friendRepository.findByUsers_IdAndUserFriend_Id(friendsId, tokenUserId);

		boolean isFriend = false;
		FriendStatus status = null;

		if(friendsOptional.isPresent()) {
			Friend friend = friendsOptional.get();
			status = friend.getStatus();
		}

		return new FriendsCheckResponse(isFriend, status);
	}
}