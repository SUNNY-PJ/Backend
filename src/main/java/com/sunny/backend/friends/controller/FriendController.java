package com.sunny.backend.friends.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.friends.dto.response.FriendListResponse;
import com.sunny.backend.friends.service.FriendService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "5. Friends", description = "Friends API")
@RequestMapping(value = "/friends")
@RestController
@RequiredArgsConstructor
public class FriendController {
	private final FriendService friendService;
	private final ResponseService responseService;

	@ApiOperation(tags = "5. Friends", value = "친구 목록 가져오기")
	@GetMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<FriendListResponse>> getFriends(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		FriendListResponse friendListResponse = friendService.getFriends(customUserPrincipal);
		return responseService.getSingleResponse(HttpStatus.OK.value(), friendListResponse, "친구 목록 가져오기");
	}

	@ApiOperation(tags = "5. Friends", value = "친구 신청하기")
	@PostMapping("/{userFriendId}")
	public ResponseEntity<CommonResponse.GeneralResponse> addFriend(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "userFriendId") Long userFriendId) throws IOException {
		friendService.addFriend(customUserPrincipal, userFriendId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "친구 신청 성공");
	}

	@ApiOperation(tags = "5. Friends", value = "친구 승인하기")
	@PostMapping("/approve/{friendId}")
	public ResponseEntity<CommonResponse.GeneralResponse> approveFriend(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) throws IOException {
		friendService.approveFriend(customUserPrincipal, friendId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "승인 되었습니다.");
	}

	@ApiOperation(tags = "5. Friends", value = "친구 거절하기")
	@DeleteMapping("/approve/{friendId}")
	public ResponseEntity<CommonResponse.GeneralResponse> refuseFriend(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		friendService.refuseFriend(customUserPrincipal, friendId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "거절 되었습니다.");
	}

	@ApiOperation(tags = "5. Friends", value = "친구 끊기")
	@DeleteMapping("/{friendId}")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteFriends(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		friendService.deleteFriends(customUserPrincipal, friendId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "친구를 끊었습니다.");
	}

}
