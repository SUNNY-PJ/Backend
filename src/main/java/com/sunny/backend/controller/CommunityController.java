package com.sunny.backend.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.community.CommunityRequest;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.SortType;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.community.CommunityService;

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
	@GetMapping("")
	public ResponseEntity<Slice<CommunityResponse.PageResponse>> getCommunityList(
		@RequestParam(required = false) SortType sort,
		@RequestParam(required = false) BoardType boardType,
		@RequestParam(required = false) String search,
		Pageable pageable) {
		Slice<CommunityResponse.PageResponse> responseDTO;
		//검색조건 중 모든 내용을 입력하지 않고 요청을 보냈을 때 일반 목록 페이지 출력
		if (search == null && boardType == null && sort == null) {
			responseDTO = communityService.getCommunityList(pageable);
		} else {
			responseDTO = communityService.getPageListWithSearch(sort, boardType, search, pageable);

		}
		return ResponseEntity.ok().body(responseDTO);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 상세 조회")
	@GetMapping("/{communityId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> getCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long communityId) {
		System.out.println("sgsgsgg");
		return communityService.findCommunity(customUserPrincipal, communityId);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 등록")
	@PostMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> createCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestPart(value = "communityRequest") CommunityRequest communityRequest,
		@RequestPart(required = false) List<MultipartFile> files) {
		return communityService.createCommunity(customUserPrincipal, communityRequest, files);
	}

	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 수정")
	@PutMapping("/{communityId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> updateCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId,
		@RequestPart(value = "communityRequest") CommunityRequest communityRequest,
		@RequestPart(required = false) List<MultipartFile> files) {

		return communityService.updateCommunity(customUserPrincipal, communityId, communityRequest, files);
	}

	//게시글 삭제
	@ApiOperation(tags = "2. Community", value = "커뮤니티 게시글 삭제")
	@DeleteMapping("/{communityId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> deleteCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		return communityService.deleteCommunity(customUserPrincipal, communityId);
	}
}

