package com.sunny.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.response.ProfileResponse;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.community.dto.CommunityResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.MyPageService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "8. MyPage", description = "My Page API")
@RequestMapping(value = "/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @ApiOperation(tags = "8. MyPage", value = "프로필 조회")
    @GetMapping("")
    public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> getMypage(
            @AuthUser CustomUserPrincipal customUserPrincipal){
        return myPageService.getMypage(customUserPrincipal);
    }
    @ApiOperation(tags = "8. MyPage", value = "작성 글 가져오기")
    @GetMapping("/mycommunity")
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse.PageResponse>> getCommunityList(@AuthUser CustomUserPrincipal customUserPrincipal) {
        return myPageService.getMyCommunity(customUserPrincipal);
    }

    @ApiOperation(tags = "8. MyPage", value = "스크랩 글 가져오기")
    @GetMapping("/myscrap")
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse>> getScrapList(@AuthUser CustomUserPrincipal customUserPrincipal) {
        return myPageService.getScrapByUserId(customUserPrincipal);
    }

    @ApiOperation(tags = "8. MyPage", value = "댓글 가져오기")
    @GetMapping("/mycomment")
    public ResponseEntity<CommonResponse.ListResponse<CommentResponse.Mycomment>> getCommentList(@AuthUser CustomUserPrincipal customUserPrincipal) {
        return myPageService.getCommentByUserId(customUserPrincipal);
    }

    @ApiOperation(tags = "8. MyPage", value = "프로필 설정")
    @PostMapping("/profile")
    public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @RequestPart(value = "name",required = false) String name,
            @RequestPart(value = "profile",required = false) MultipartFile profile){
        return myPageService.updateProfile(customUserPrincipal,name,profile);
    }
    //이거 일단 임시 테스트임
    @ApiOperation(tags = "8. MyPage", value = "로그아웃")
    @GetMapping("/auth/kakao/logout")
    public ResponseEntity<CommonResponse.GeneralResponse> handleKakaoLogout(@RequestParam(name = "client_id") String clientId,
                                    @RequestParam(name = "logout_redirect_uri") String logoutRedirectUri) {
        CommonResponse.GeneralResponse response = new CommonResponse.GeneralResponse(HttpStatus.OK.value(), "Logout 성공");
        return ResponseEntity.ok().body(response);
    }
    @ApiOperation(tags = "8. MyPage", value = "탈퇴")
    @GetMapping("/auth/leave")
    public ResponseEntity<CommonResponse.GeneralResponse> deleteAccount(
            @AuthUser CustomUserPrincipal customUserPrincipal) {
        return myPageService.deleteAccount(customUserPrincipal);
    }
}
