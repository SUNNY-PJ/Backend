package com.sunny.backend.user.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.report.dto.ReportRequest;
import com.sunny.backend.report.dto.ReportStatusRequest;
import com.sunny.backend.user.dto.response.ProfileResponse;
import com.sunny.backend.user.dto.response.UserCommentResponse;
import com.sunny.backend.user.dto.response.UserCommunityResponse;
import com.sunny.backend.user.dto.response.UserReportResponse;
import com.sunny.backend.user.dto.response.UserScrapResponse;
import com.sunny.backend.user.service.UserService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "0. User", description = "User API")
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@ApiOperation(tags = "0. User", value = "프로필 조회")
	@GetMapping("")
	public ResponseEntity<ProfileResponse> getUserProfile(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(name = "userId", required = false) Long userId) {
		ProfileResponse profileResponse = userService.getUserProfile(customUserPrincipal, userId);
		return ResponseEntity.ok().body(profileResponse);
	}

	@ApiOperation(tags = "0. User", value = "작성 글 가져오기")
	@GetMapping("/community")
	public ResponseEntity<List<UserCommunityResponse>> getUserCommunityList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(name = "userId", required = false) Long userId) {
		List<UserCommunityResponse> userCommunityResponses =
			userService.getUserCommunityList(customUserPrincipal, userId);
		return ResponseEntity.ok().body(userCommunityResponses);
	}

	@ApiOperation(tags = "0. User", value = "스크랩 글 가져오기")
	@GetMapping("/scrap")
	public ResponseEntity<List<UserScrapResponse>> getScrapList(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		List<UserScrapResponse> userScrapResponses = userService.getScrapList(customUserPrincipal);
		return ResponseEntity.ok().body(userScrapResponses);
	}

	@ApiOperation(tags = "0. User", value = "댓글 가져오기")
	@GetMapping("/comment")
	public ResponseEntity<List<UserCommentResponse>> getCommentList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(name = "userId", required = false) Long userId) {
		List<UserCommentResponse> userCommentResponses = userService.getCommentByUserId(customUserPrincipal, userId);
		return ResponseEntity.ok().body(userCommentResponses);
	}

	@ApiOperation(tags = "0. User", value = "프로필 설정")
	@PostMapping("/profile")
	public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestPart(value = "profile", required = false) MultipartFile profile) {
		return userService.updateProfile(customUserPrincipal, profile);
	}

	@ApiOperation(tags = "0. User", value = "유저 신고")
	@PostMapping("/report")
	public ResponseEntity<UserReportResponse> reportCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @RequestBody ReportRequest reportRequest) {
		UserReportResponse userReportResponse = userService.reportCommunity(customUserPrincipal, reportRequest);
		return ResponseEntity.ok().body(userReportResponse);
	}

	@ApiOperation(tags = "0. User", value = "유저 신고 승인")
	@PatchMapping("/report")
	public ResponseEntity<CommonResponse.GeneralResponse> approveUserReport(
		@RequestBody ReportStatusRequest reportStatusRequest) throws IOException {
		return userService.approveUserReport(reportStatusRequest);
	}

	@ApiOperation(tags = "0. User", value = "유저 신고 거절")
	@DeleteMapping("/report")
	public ResponseEntity<CommonResponse.GeneralResponse> refuseUserReport(
		@RequestBody ReportStatusRequest reportStatusRequest) {
		return userService.refuseUserReport(reportStatusRequest);
	}
}
