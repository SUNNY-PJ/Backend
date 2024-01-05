package com.sunny.backend.friends.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.FriendsApproveRequest;
import com.sunny.backend.dto.response.FriendsCheckResponse;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.domain.Friends;
import com.sunny.backend.friends.repository.FriendsRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendsService {
	private final ResponseService responseService;
	private final FriendsRepository friendsRepository;
	private final UserRepository userRepository;

	public ResponseEntity<CommonResponse.ListResponse<FriendsResponse>> getFriendsList(
		CustomUserPrincipal customUserPrincipal, FriendStatus friendStatus) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		List<FriendsResponse> friendsResponses =
			friendsRepository.findByUsers_IdAndStatus(tokenUserId, friendStatus)
				.stream()
				.map(FriendsResponse::from)
				.toList();
		return responseService.getListResponse(HttpStatus.OK.value(), friendsResponses, "친구 목록 가져오기");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> addFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsId) {
		Users user = customUserPrincipal.getUsers();
		Users friend = userRepository.getById(friendsId);

		Friends friends = Friends.builder()
			.users(friend)
			.friend(user)
			.status(FriendStatus.WAIT)
			.build();
		friendsRepository.save(friends);

		return responseService.getGeneralResponse(HttpStatus.OK.value(),
			user.getName() + "이 " + friend.getName() + "에게 친구 신청했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> approveFriends(
		CustomUserPrincipal customUserPrincipal, FriendsApproveRequest request) {
		Friends friends = friendsRepository.getById(request.friendsSn());
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		friends.validateFriendsByUser(friends.getUsers().getId(), tokenUserId);

		if(request.approve()) {
			friends.approveStatus();
			Friends friendsUser = Friends.builder()
				.users(friends.getFriend())
				.friend(friends.getUsers())
				.status(FriendStatus.APPROVE)
				.build();
			friendsRepository.save(friendsUser);
			return responseService.getGeneralResponse(HttpStatus.OK.value(), "승인 되었습니다.");
		} else {
			friendsRepository.deleteById(friends.getFriendsSn());
			return responseService.getGeneralResponse(HttpStatus.OK.value(), "거절 되었습니다.");
		}
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsSn) {
		Friends friends = friendsRepository.getById(friendsSn);
		friends.validateFriendsByUser(friends.getUsers().getId(), customUserPrincipal.getUsers().getId());
		friendsRepository.deleteById(friendsSn);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "삭제 완료");
	}

	public ResponseEntity<CommonResponse.SingleResponse<FriendsCheckResponse>> checkFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsId) {
		Long tokenUserId = customUserPrincipal.getUsers().getId();
		Optional<Friends> friendsOptional = friendsRepository.findByUsers_IdAndFriend_Id(friendsId, tokenUserId);

		if(friendsOptional.isPresent()) {
			Friends friends = friendsOptional.get();
			switch (friends.getStatus()) {
				case WAIT -> {
					return responseService.getSingleResponse(
						HttpStatus.OK.value(),
						new FriendsCheckResponse(false, FriendStatus.WAIT),
						"승인 대기중"
					);
				}
				case APPROVE -> {
					return responseService.getSingleResponse(
						HttpStatus.OK.value(),
						new FriendsCheckResponse(true, FriendStatus.APPROVE),
						"친구 입니다."
					);
				}
			}
		}
		return responseService.getSingleResponse(
			HttpStatus.OK.value(),
			new FriendsCheckResponse(false, null),
			"친구가 아닙니다."
		);
	}
}