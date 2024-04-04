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
import com.sunny.backend.common.response.ServerResponse;
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
	public ResponseEntity<ServerResponse<List<CommunityPageResponse>>> getCommunityList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(required = false) Long communityId,
		@RequestParam(required = false) SortType sortType,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize,
		@RequestParam(required = false) BoardType boardType,
		@RequestParam(required = false) String search
	) {
		List<CommunityPageResponse> response = communityService.paginationNoOffsetBuilder(
			customUserPrincipal, communityId, sortType, boardType, search, pageSize);
		return ServerResponse.ok(response, "게시판을 성공적으로 조회했습니다.");
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 상세 조회")
	@GetMapping("/{communityId}")
	public ResponseEntity<ServerResponse<CommunityResponse>> getCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long communityId
	) {
		CommunityResponse response = communityService.findCommunity(customUserPrincipal, communityId);
		return ServerResponse.ok(response, "게시글을 성공적으로 불러왔습니다.");
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 등록")
	@PostMapping("")
	public ResponseEntity<ServerResponse<CommunityResponse>> createCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestPart(value = "communityRequest") CommunityRequest communityRequest,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) {
		CommunityResponse response = communityService.createCommunity(customUserPrincipal, communityRequest, files);
		return ServerResponse.ok(response, "게시글을 성공적으로 작성했습니다.");
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 수정")
	@PatchMapping("/{communityId}")
	public ResponseEntity<ServerResponse<CommunityResponse>> updateCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId,
		@Valid @RequestPart(value = "communityRequest") CommunityRequest communityRequest,
		@RequestPart(required = false) List<MultipartFile> files
	) {
		CommunityResponse response = communityService.updateCommunity(customUserPrincipal, communityId,
			communityRequest, files);
		return ServerResponse.ok(response, "게시글 수정을 완료했습니다.");
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 삭제")
	@DeleteMapping("/{communityId}")
	public ResponseEntity<ServerResponse<Void>> deleteCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long communityId
	) {
		communityService.deleteCommunity(customUserPrincipal, communityId);
		return ServerResponse.message("게시글을 삭제했습니다.");
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 조회수/댓글수 확인")
	@GetMapping("/count/{communityId}")
	public ResponseEntity<ServerResponse<ViewAndCommentResponse>> getCommentAndViewByCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long communityId
	) {
		ViewAndCommentResponse response = communityService.getCommentAndViewByCommunity(communityId);
		return ServerResponse.ok(response, "게시글 조회수와 댓글수를 불러왔습니다.");
	}
}