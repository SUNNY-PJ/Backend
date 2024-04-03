package com.sunny.backend.community.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.SortType;
import com.sunny.backend.community.dto.request.CommunityRequest;
import com.sunny.backend.community.dto.response.CommunityPageResponse;
import com.sunny.backend.community.dto.response.CommunityResponse;
import com.sunny.backend.community.dto.response.ViewAndCommentResponse;
import com.sunny.backend.community.service.CommunityService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "2. Community", description = "Community API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

	private final CommunityService communityService;

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시판 목록 조회")
	@GetMapping("/board")
	public ResponseEntity<CommonResponse.SingleResponse<List<CommunityPageResponse>>> getCommunityList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(required = false) Long communityId,
		@RequestParam(required = false) SortType sortType,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize,
		@RequestParam(required = false) BoardType boardType,
		@RequestParam(required = false) String search) {
		return communityService.paginationNoOffsetBuilder(customUserPrincipal, communityId, sortType, boardType, search,
			pageSize);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 상세 조회")
	@GetMapping("/{communityId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> getCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long communityId) {
		return communityService.findCommunity(customUserPrincipal, communityId);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 등록")
	@PostMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> createCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestPart(value = "communityRequest") CommunityRequest communityRequest,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		return communityService.createCommunity(customUserPrincipal, communityRequest, files);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 수정")
	@PatchMapping("/{communityId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> updateCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId,
		@Valid @RequestPart(value = "communityRequest") CommunityRequest communityRequest,
		@RequestPart(required = false) List<MultipartFile> files) {
		return communityService.updateCommunity(customUserPrincipal, communityId, communityRequest, files);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 삭제")
	@DeleteMapping("/{communityId}")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		return communityService.deleteCommunity(customUserPrincipal, communityId);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 조회수/댓글수 확인")
	@GetMapping("/count/{communityId}")
	public ResponseEntity<CommonResponse.SingleResponse<ViewAndCommentResponse>> getCommentAndViewByCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		return communityService.getCommentAndViewByCommunity(customUserPrincipal, communityId);
	}
}