package com.sunny.backend.friends.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.response.FriendCheckResponse;
import com.sunny.backend.dto.response.FriendResponse;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.friends.service.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<CommonResponse.ListResponse<FriendResponse>> getFriends(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @NotBlank(message = "친구 상태가 입력되지 않았습니다.")
        @RequestParam(name = "friendStatus") FriendStatus friendStatus) {
        List<FriendResponse> friendRespons = friendService.getFriends(customUserPrincipal, friendStatus);
        return responseService.getListResponse(HttpStatus.OK.value(), friendRespons, "친구 목록 가져오기");
    }

    @ApiOperation(tags = "5. Friends", value = "친구인지 확인하기")
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse.SingleResponse<FriendCheckResponse>> checkFriend(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "userId") Long userFriendId) {
        FriendCheckResponse friendCheckResponse = friendService.checkFriend(customUserPrincipal, userFriendId);
        return responseService.getSingleResponse(HttpStatus.OK.value(), friendCheckResponse, "");
    }

    @ApiOperation(tags = "5. Friends", value = "친구 신청하기")
    @PostMapping("/{userId}")
    public ResponseEntity<CommonResponse.GeneralResponse> addFriend(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "userId") Long userFriendId) {
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
