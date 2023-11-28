package com.sunny.backend.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.FriendsApproveRequest;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.entity.friends.ApproveType;
import com.sunny.backend.entity.friends.Friends;
import com.sunny.backend.repository.FriendsRepository;
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

	public CommonResponse.ListResponse<FriendsResponse> getFriendsList(CustomUserPrincipal customUserPrincipal) {
		List<FriendsResponse> responseList = new ArrayList<>();
		for (Friends friends : friendsRepository.findByUsers_IdAndApproveNot(customUserPrincipal.getUsers().getId(), ApproveType.REFUSE)) {
			responseList.add(FriendsResponse.builder()
					.friendsSn(friends.getFriendsSn())
					.friendsId(friends.getFriends().getId())
					.friendsName(friends.getFriends().getName())
					.friendsProfile(friends.getFriends().getProfile())
					.approveType(friends.getApprove().getStatus())
				.build());
		}
		return responseService.getListResponse(HttpStatus.OK.value(), responseList, "");
	}

	public CommonResponse.GeneralResponse addFriends(CustomUserPrincipal customUserPrincipal, Long friendsUserId) {
		Users user = customUserPrincipal.getUsers();
		Users friend = userRepository.findById(friendsUserId).orElseThrow(() -> new IllegalArgumentException("친구가 존재하지 않습니다."));

		Friends friendsUser = Friends.builder()
			.users(user)
			.friends(friend)
			.approve(ApproveType.WAIT)
			.build();
		friendsRepository.save(friendsUser);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), user.getName() + "이 " + friend.getName() +"에게 친구 신청했습니다.");
	}

	@Transactional
	public CommonResponse.GeneralResponse approveFriends(CustomUserPrincipal customUserPrincipal, FriendsApproveRequest request) {
		Friends userFriends = friendsRepository.findByFriendsSnAndUsers_Id(request.getFriendsSn(),
			customUserPrincipal.getUsers().getId()).orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));

		userFriends.setApprove(request.getApprove());
		String msg =request.getApprove().getStatus() + "되었습니다";

		Friends friendsUser = Friends.builder()
				.users(userFriends.getFriends())
				.friends(userFriends.getUsers())
				.approve(request.getApprove())
				.build();
		friendsRepository.save(friendsUser);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), msg);
	}

	public CommonResponse.GeneralResponse deleteFriends(CustomUserPrincipal customUserPrincipal, Long friendsId) {
		Friends friends = friendsRepository.findById(friendsId).orElseThrow(() -> new IllegalArgumentException("친구가 존재하지 않습니다."));
		if(!friends.getUsers().getId().equals(customUserPrincipal.getUsers().getId())) {
			return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "권한이 없습니다.");
		}
		friendsRepository.deleteById(friendsId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "삭제 완료");
	}
}
