package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.FriendsApproveRequest;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.entity.friends.ApproveType;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.FriendsService;
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
            @AuthUser CustomUserPrincipal customUserPrincipal, @RequestParam(name = "type") ApproveType approveType) {
        return friendsService.getFriendsList(customUserPrincipal, approveType);
    }

    @ApiOperation(tags = "5. Friends", value = "친구 추가하기")
    @PostMapping("/{user_id}")
    public ResponseEntity<CommonResponse.GeneralResponse> addFriends(@AuthUser CustomUserPrincipal customUserPrincipal,
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

    @ApiOperation(tags = "5. Friends", value = "친구 삭제하기")
    @DeleteMapping("/{friends_id}")
    public ResponseEntity<CommonResponse.GeneralResponse> deleteFriends(
            @AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "friends_id") Long friendsId) {
        return friendsService.deleteFriends(customUserPrincipal, friendsId);
    }

}
