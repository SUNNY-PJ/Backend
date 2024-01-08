package com.sunny.backend.friends.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.FriendsApproveRequest;
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
	private final ResponseService responseService;
	private final FriendRepository friendRepository;
	private final UserRepository userRepository;

	public ResponseEntity<CommonResponse.ListResponse<FriendsResponse>> getFriendsList(
		CustomUserPrincipal customUserPrincipal, @Valid FriendStatus friendStatus) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();

		List<FriendsResponse> friendsResponses =
			friendRepository.findByUsers_IdAndStatus(tokenUserId, friendStatus)
				.stream()
				.map(FriendsResponse::from)
				.toList();

		return responseService.getListResponse(HttpStatus.OK.value(), friendsResponses, "친구 목록 가져오기");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> addFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsId) {
		Users user = customUserPrincipal.getUsers();
		Users userFriend = userRepository.getById(friendsId);

		getByUserAndUserFriend(user, userFriend, FriendStatus.WAIT);

		return responseService.getGeneralResponse(HttpStatus.OK.value(),
			user.getName() + "이 " + userFriend.getName() + "에게 친구 신청했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> approveFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsSn, @Valid FriendsApproveRequest request) {
		Friend friend = friendRepository.getById(friendsSn);
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friend.validateFriendsByUser(friend.getUsers().getId(), tokenUserId);

		if(request.approve()) {
			friend.approveStatus();
			getByUserAndUserFriend(friend.getUsers(), friend.getUserFriend(), FriendStatus.APPROVE);
			return responseService.getGeneralResponse(HttpStatus.OK.value(), "승인 되었습니다.");
		} else {
			friendRepository.deleteById(friend.getId());
			return responseService.getGeneralResponse(HttpStatus.OK.value(), "거절 되었습니다.");
		}
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
	public ResponseEntity<CommonResponse.GeneralResponse> deleteFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsSn) {
		Friend friend = friendRepository.getById(friendsSn);
		friend.validateFriendsByUser(friend.getUsers().getId(), customUserPrincipal.getUsers().getId());

		friendRepository.deleteById(friendsSn);

		return responseService.getGeneralResponse(HttpStatus.OK.value(), "삭제 완료");
	}

	public ResponseEntity<CommonResponse.SingleResponse<FriendsCheckResponse>> checkFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsId) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		Optional<Friend> friendsOptional = friendRepository.findByUsers_IdAndUserFriend_Id(friendsId, tokenUserId);

		boolean isFriend = false;
		FriendStatus status = null;

		if(friendsOptional.isPresent()) {
			Friend friend = friendsOptional.get();
			status = friend.getStatus();
		}

		return responseService.getSingleResponse(
			HttpStatus.OK.value(), new FriendsCheckResponse(isFriend, status)
			, status != null ? status.getStatus() : null
		);
	}
}