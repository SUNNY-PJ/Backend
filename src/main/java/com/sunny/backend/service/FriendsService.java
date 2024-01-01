package com.sunny.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.FriendsApproveRequest;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.entity.friends.ApproveType;
import com.sunny.backend.entity.friends.Friends;
import com.sunny.backend.repository.friends.FriendsRepository;
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
		CustomUserPrincipal customUserPrincipal,
		ApproveType approveType) {
		List<FriendsResponse> responseList = new ArrayList<>(
			friendsRepository.getFindUserIdAndApproveType(customUserPrincipal.getUsers().getId(), approveType));
		return responseService.getListResponse(HttpStatus.OK.value(), responseList, "친구 목록 가져오기");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> addFriends(CustomUserPrincipal customUserPrincipal,
		Long friendsUserId) {
		Users user = customUserPrincipal.getUsers();
		Users friend = userRepository.findById(friendsUserId)
			.orElseThrow(() -> new IllegalArgumentException("친구가 존재하지 않습니다."));

		Friends friendsUser = Friends.builder()
			.users(user)
			.friend(friend)
			.approve(ApproveType.WAIT)
			.build();
		friendsRepository.save(friendsUser);
		return responseService.getGeneralResponse(HttpStatus.OK.value(),
			user.getName() + "이 " + friend.getName() + "에게 친구 신청했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> approveFriends(CustomUserPrincipal customUserPrincipal,
		FriendsApproveRequest request) {
		Friends userFriends = friendsRepository.findByFriendsSnAndUsers_Id(request.getFriendsSn(),
				customUserPrincipal.getUsers().getId())
			.orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));

		userFriends.setApprove(request.getApprove());
		String msg = request.getApprove().getStatus() + "되었습니다";

		Friends friendsUser = Friends.builder()
			.users(userFriends.getFriend())
			.friend(userFriends.getUsers())
			.approve(request.getApprove())
			.build();
		friendsRepository.save(friendsUser);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), msg);
	}

	public ResponseEntity<CommonResponse.GeneralResponse> deleteFriends(CustomUserPrincipal customUserPrincipal,
		Long friendsId) {
		Friends friends = friendsRepository.findById(friendsId)
			.orElseThrow(() -> new IllegalArgumentException("친구가 존재하지 않습니다."));
		if (!friends.getUsers().getId().equals(customUserPrincipal.getUsers().getId())) {
			return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "권한이 없습니다.");
		}
		friendsRepository.deleteById(friendsId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "삭제 완료");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> checkFriends(
		CustomUserPrincipal customUserPrincipal, Long friendsId) {
		Optional<Friends> friendsOptional = friendsRepository.findByUsers_IdAndFriendsSn(
			customUserPrincipal.getUsers().getId(), friendsId);

		if(friendsOptional.isPresent()) {
			Friends friends = friendsOptional.get();
			switch (friends.getApprove()) {
				case WAIT -> {
					return responseService.getGeneralResponse(HttpStatus.OK.value(), "대기중");
				}
				case APPROVE -> {
					return responseService.getGeneralResponse(HttpStatus.OK.value(), "친구");
				}
				case REFUSE -> {
					return responseService.getGeneralResponse(HttpStatus.OK.value(), "친구 아님");
				}
			}
			return null;
		} else {
			return responseService.getGeneralResponse(HttpStatus.OK.value(), "친구 아님");
		}
	}
}