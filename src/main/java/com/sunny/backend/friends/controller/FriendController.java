package com.sunny.backend.friends.controller;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.friends.dto.response.FriendCheckResponse;
import com.sunny.backend.friends.dto.response.FriendStatusResponse;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.friends.service.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. Friends", description = "Friends API")
@RequestMapping(value = "/api/v1/friends")
@RestController
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final ResponseService responseService;

    @ApiOperation(tags = "5. Friends", value = "친구 목록 가져오기")
    @GetMapping("")
    public ResponseEntity<CommonResponse.SingleResponse<FriendStatusResponse>> getFriends(
        @AuthUser CustomUserPrincipal customUserPrincipal) {
        FriendStatusResponse friendStatusResponse = friendService.getFriends(customUserPrincipal);
        return responseService.getSingleResponse(HttpStatus.OK.value(), friendStatusResponse, "친구 목록 가져오기");
    }

    @ApiOperation(tags = "5. Friends", value = "친구인지 확인하기")
    @GetMapping("/{userFriendId}")
    public ResponseEntity<CommonResponse.SingleResponse<FriendCheckResponse>> checkFriend(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "userFriendId") Long userFriendId) {
        FriendCheckResponse friendCheckResponse = friendService.checkFriend(customUserPrincipal, userFriendId);
        return responseService.getSingleResponse(HttpStatus.OK.value(), friendCheckResponse, "");
    }

    @ApiOperation(tags = "5. Friends", value = "친구 신청하기")
    @PostMapping("/{userFriendId}")
    public ResponseEntity<CommonResponse.GeneralResponse> addFriend(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "userFriendId") Long userFriendId) {
        friendService.addFriend(customUserPrincipal, userFriendId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "친구 신청 성공");
    }

    @ApiOperation(tags = "5. Friends", value = "친구 승인하기")
    @PostMapping("/approve/{friendId}")
    public ResponseEntity<CommonResponse.GeneralResponse> approveFriend(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "friendId") Long friendId) {
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
