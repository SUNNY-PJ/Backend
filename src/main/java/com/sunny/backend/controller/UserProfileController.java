package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.response.ProfileResponse;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.community.dto.CommunityResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.UserProfileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "10. Profile", description = "Profile API")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;
    @ApiOperation(tags = "10. Profile", value = "커뮤니티 친구 프로필 조회")
    @GetMapping("/{communityId}")
    public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> getUserProfile(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @PathVariable Long communityId) {
        return userProfileService.getUserProfile(customUserPrincipal, communityId);
    }

    @ApiOperation(tags = "10. Profile", value = "커뮤니티 친구 작성 글 조회")
    @GetMapping("/{communityId}/community")
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse.PageResponse>> getFriendsCommunity(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @PathVariable Long communityId) {
        return userProfileService.getFriendsCommunity(customUserPrincipal, communityId);
    }


    @ApiOperation(tags = "10. Profile", value = "커뮤니티 친구가 쓴 댓글 조회")
    @GetMapping("/{communityId}/comment")
    public ResponseEntity<CommonResponse.ListResponse<CommentResponse.Mycomment>> getCommentByFriendsId(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @PathVariable Long communityId) {
        return userProfileService.getCommentByFriendsId(customUserPrincipal, communityId);
    }


}
