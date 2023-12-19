package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.dto.response.ProfileResponse;
import com.sunny.backend.dto.response.ScrapResponse;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.MyPageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "8. MyPage", description = "My Page API")
@RequestMapping(value="/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;
    @ApiOperation(tags = "8. MyPage", value = "작성 글 가져오기")
    @GetMapping("")
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse.PageResponse>> getCommunityList(@AuthUser CustomUserPrincipal customUserPrincipal) {
        return myPageService.getMyCommunity(customUserPrincipal);
    }

    @ApiOperation(tags = "8. MyPage - Scrap", value = "스크랩 글 가져오기")
    @GetMapping("/myscrap")
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse>> getScrapList(@AuthUser CustomUserPrincipal customUserPrincipal) {
        return myPageService.getScrapByUserId(customUserPrincipal);
    }

    @ApiOperation(tags = "8. MyPage - Comment", value = "댓글 가져오기")
    @GetMapping("/mycomment")
    public ResponseEntity<CommonResponse.ListResponse<CommentResponse>> getCommentList(@AuthUser CustomUserPrincipal customUserPrincipal) {
        return myPageService.getCommentByUserId(customUserPrincipal);
    }

    @ApiOperation(tags = "8. MyPage - Profile", value = "프로필 설정")
    @GetMapping("/profile")
    public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @RequestPart MultipartFile profile,
            @RequestParam String nickname) {
        return myPageService.updateProfile(customUserPrincipal,profile,nickname);
    }

}
