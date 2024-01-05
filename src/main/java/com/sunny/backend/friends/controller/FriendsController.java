package com.sunny.backend.friends.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.FriendsApproveRequest;
import com.sunny.backend.dto.response.FriendsCheckResponse;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.friends.service.FriendsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. Friends", description = "Friends API")
@RequestMapping(value = "/api/v1/friends")
@RestController
@RequiredArgsConstructor
public class FriendsController {
    private final FriendsService friendsService;

    @ApiOperation(tags = "5. Friends", value = "친구 목록 가져오기")
    @GetMapping("")
    public ResponseEntity<CommonResponse.ListResponse<FriendsResponse>> getFriendsList(
            @AuthUser CustomUserPrincipal customUserPrincipal, @RequestParam(name = "friendStatus") FriendStatus friendStatus) {
        return friendsService.getFriendsList(customUserPrincipal, friendStatus);
    }

    @ApiOperation(tags = "5. Friends", value = "친구인지 확인하기")
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse.SingleResponse<FriendsCheckResponse>> checkFriends(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "userId") Long friendsId) {
        return friendsService.checkFriends(customUserPrincipal, friendsId);
    }

    @ApiOperation(tags = "5. Friends", value = "친구 신청하기")
    @PostMapping("/{user_id}")
    public ResponseEntity<CommonResponse.GeneralResponse> addFriends(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "user_id") Long friendsId) {
        return friendsService.addFriends(customUserPrincipal, friendsId);
    }

    @ApiOperation(tags = "5. Friends", value = "친구 승인하기")
    @PostMapping("/approve")
    public ResponseEntity<CommonResponse.GeneralResponse> approveFriends(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @RequestBody FriendsApproveRequest friendsApproveRequest) {
        return friendsService.approveFriends(customUserPrincipal, friendsApproveRequest);
    }

    @ApiOperation(tags = "5. Friends", value = "친구 끊기")
    @DeleteMapping("/{friendsSn}")
    public ResponseEntity<CommonResponse.GeneralResponse> deleteFriends(
            @AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "friendsSn") Long friendsSn) {
        return friendsService.deleteFriends(customUserPrincipal, friendsSn);
    }

}
