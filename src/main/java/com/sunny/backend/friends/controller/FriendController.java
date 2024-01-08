package com.sunny.backend.friends.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.FriendsApproveRequest;
import com.sunny.backend.dto.response.FriendsCheckResponse;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.friends.service.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. Friends", description = "Friends API")
@RequestMapping(value = "/api/v1/friends")
@RestController
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @ApiOperation(tags = "5. Friends", value = "친구 목록 가져오기")
    @GetMapping("")
    public ResponseEntity<CommonResponse.ListResponse<FriendsResponse>> getFriendsList(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @NotBlank(message = "친구 상태가 입력되지 않았습니다.")
        @RequestParam(name = "friendStatus") FriendStatus friendStatus) {
        return friendService.getFriendsList(customUserPrincipal, friendStatus);
    }

    @ApiOperation(tags = "5. Friends", value = "친구인지 확인하기")
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse.SingleResponse<FriendsCheckResponse>> checkFriends(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "userId") Long friendsId) {
        return friendService.checkFriends(customUserPrincipal, friendsId);
    }

    @ApiOperation(tags = "5. Friends", value = "친구 신청하기")
    @PostMapping("/{userId}")
    public ResponseEntity<CommonResponse.GeneralResponse> addFriends(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "userId") Long friendsId) {
        return friendService.addFriends(customUserPrincipal, friendsId);
    }

    @ApiOperation(tags = "5. Friends", value = "친구 승인하기")
    @PostMapping("/approve/{friendsSn}")
    public ResponseEntity<CommonResponse.GeneralResponse> approveFriends(
        @AuthUser CustomUserPrincipal customUserPrincipal,
        @PathVariable(name = "friendsSn") Long friendsSn,
        @Valid @RequestBody FriendsApproveRequest friendsApproveRequest) {
        return friendService.approveFriends(customUserPrincipal, friendsSn, friendsApproveRequest);
    }

    @ApiOperation(tags = "5. Friends", value = "친구 끊기")
    @DeleteMapping("/{friendsSn}")
    public ResponseEntity<CommonResponse.GeneralResponse> deleteFriends(
        @AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "friendsSn") Long friendsSn) {
        return friendService.deleteFriends(customUserPrincipal, friendsSn);
    }

}
