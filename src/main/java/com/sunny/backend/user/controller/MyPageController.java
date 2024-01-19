package com.sunny.backend.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.user.dto.ProfileResponse;
import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.community.dto.response.CommunityResponse;
import com.sunny.backend.user.dto.ScrapResponse;
import com.sunny.backend.user.service.MyPageService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "0. User", description = "User API")
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class MyPageController {

	private final MyPageService myPageService;

	@ApiOperation(tags = "0. User", value = "프로필 조회")
	@GetMapping("")
	public ResponseEntity<ProfileResponse> getUserProfile(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(name = "userId", required = false) Long userId) {
		ProfileResponse profileResponse = myPageService.getUserProfile(customUserPrincipal, userId);
		return ResponseEntity.ok().body(profileResponse);
	}

	@ApiOperation(tags = "0. User", value = "작성 글 가져오기")
	@GetMapping("/community")
	public ResponseEntity<List<CommunityResponse.PageResponse>> getUserCommunityList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(name = "userId", required = false) Long userId) {
		List<CommunityResponse.PageResponse> pageResponse =
			myPageService.getUserCommunityList(customUserPrincipal, userId);
		return ResponseEntity.ok().body(pageResponse);
	}

	@ApiOperation(tags = "0. User", value = "스크랩 글 가져오기")
	@GetMapping("/scrap")
	public ResponseEntity<List<ScrapResponse>> getScrapList(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		List<ScrapResponse> scrapResponses = myPageService.getScrapList(customUserPrincipal);
		return ResponseEntity.ok().body(scrapResponses);
	}

	@ApiOperation(tags = "0. User", value = "댓글 가져오기")
	@GetMapping("/comment")
	public ResponseEntity<List<CommentResponse.MyComment>> getCommentList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(name = "userId", required = false) Long userId) {
		List<CommentResponse.MyComment> mycomments =
			myPageService.getCommentByUserId(customUserPrincipal, userId);
		return ResponseEntity.ok().body(mycomments);
	}

	@ApiOperation(tags = "0. User", value = "프로필 설정")
	@PostMapping("/profile")
	public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestPart(value = "profile", required = false) MultipartFile profile) {
		return myPageService.updateProfile(customUserPrincipal, profile);
	}

	//이거 일단 임시 테스트임
	@ApiOperation(tags = "0. User", value = "로그아웃")
	@GetMapping("/auth/kakao/logout")
	public ResponseEntity<CommonResponse.GeneralResponse> handleKakaoLogout(
		@RequestParam(name = "client_id") String clientId,
		@RequestParam(name = "logout_redirect_uri") String logoutRedirectUri) {
		CommonResponse.GeneralResponse response = new CommonResponse.GeneralResponse(
			HttpStatus.OK.value(), "Logout 성공");
		return ResponseEntity.ok().body(response);
	}

	@ApiOperation(tags = "0. User", value = "탈퇴")
	@GetMapping("/auth/leave")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteAccount(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		return myPageService.deleteAccount(customUserPrincipal);
	}
}
