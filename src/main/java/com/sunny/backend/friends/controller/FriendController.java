package com.sunny.backend.friends.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.ServerResponse;
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

	@ApiOperation(tags = "5. Friends", value = "친구 목록 가져오기")
	@GetMapping("")
	public ResponseEntity<ServerResponse<FriendListResponse>> getFriends(
		@AuthUser CustomUserPrincipal customUserPrincipal
	) {
		FriendListResponse friendListResponse = friendService.getFriends(customUserPrincipal);
		return ServerResponse.ok(friendListResponse);
	}

	@ApiOperation(tags = "5. Friends", value = "친구 신청하기")
	@PostMapping("/{userFriendId}")
	public ResponseEntity<Void> addFriend(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "userFriendId") Long userFriendId) {
		friendService.addFriend(customUserPrincipal, userFriendId);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(tags = "5. Friends", value = "친구 승인하기")
	@PostMapping("/approve/{friendId}")
	public ResponseEntity<Void> approveFriend(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		friendService.approveFriend(customUserPrincipal, friendId);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(tags = "5. Friends", value = "친구 거절하기")
	@DeleteMapping("/approve/{friendId}")
	public ResponseEntity<Void> refuseFriend(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		friendService.refuseFriend(customUserPrincipal, friendId);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(tags = "5. Friends", value = "친구 끊기")
	@DeleteMapping("/{friendId}")
	public ResponseEntity<Void> deleteFriends(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		friendService.deleteFriends(customUserPrincipal, friendId);
		return ResponseEntity.noContent().build();
	}

}
