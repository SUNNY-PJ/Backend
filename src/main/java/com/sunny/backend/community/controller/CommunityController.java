package com.sunny.backend.community.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
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
	public ResponseEntity<List<CommunityPageResponse>> getCommunityList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(required = false) Long communityId,
		@RequestParam(required = false) SortType sortType,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize,
		@RequestParam(required = false) BoardType boardType,
		@RequestParam(required = false) String search) {
		List<CommunityPageResponse> responses = communityService.paginationNoOffsetBuilder(customUserPrincipal,
			communityId, sortType, boardType, search, pageSize);
		return ResponseEntity.ok(responses);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 상세 조회")
	@GetMapping("/{communityId}")
	public ResponseEntity<CommunityResponse> getCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long communityId) {
		CommunityResponse response = communityService.findCommunity(customUserPrincipal, communityId);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 등록")
	@PostMapping
	public ResponseEntity<Void> createCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestPart(value = "communityRequest") CommunityRequest communityRequest,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) {
		Long id = communityService.createCommunity(customUserPrincipal, communityRequest, files);
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path("/{id}")
			.buildAndExpand(id).toUri();
		return ResponseEntity.created(uri)
			.build();
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 수정")
	@PutMapping("/{communityId}")
	public ResponseEntity<Void> updateCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId,
		@Valid @RequestPart(value = "communityRequest") CommunityRequest communityRequest,
		@RequestPart(required = false) List<MultipartFile> files) {
		communityService.updateCommunity(customUserPrincipal, communityId, communityRequest, files);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 삭제")
	@DeleteMapping("/{communityId}")
	public ResponseEntity<Void> deleteCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		communityService.deleteCommunity(customUserPrincipal, communityId);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 조회수/댓글수 확인")
	@GetMapping("/count/{communityId}")
	public ResponseEntity<ViewAndCommentResponse> getCommentAndViewByCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		ViewAndCommentResponse response = communityService.getCommentAndViewByCommunity(communityId);
		return ResponseEntity.ok(response);
	}
}