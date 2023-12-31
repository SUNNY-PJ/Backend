package com.sunny.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.ScrapService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "7. Scrap", description = "Scrap API")
@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
public class ScrapController {
	private final ScrapService scrapService;

	@ApiOperation(tags = "7. Scrap", value = "스크랩 조회")
	@GetMapping("")
	public ResponseEntity<CommonResponse.ListResponse<CommunityResponse>> getScrapsByUserId(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		return scrapService.getScrapsByUserId(customUserPrincipal);
	}

	@ApiOperation(tags = "7. Scrap", value = "스크랩 등록")
	@PostMapping("/{communityId}")
	public ResponseEntity<CommonResponse.GeneralResponse> addScrapToCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		return scrapService.addScrapToCommunity(customUserPrincipal, communityId);
	}

	@ApiOperation(tags = "7. Scrap", value = "스크랩 삭제")

	@DeleteMapping("/{communityId}")
	public ResponseEntity<CommonResponse.GeneralResponse> removeScrapFromCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		return scrapService.removeScrapFromCommunity(customUserPrincipal, communityId);
	}
}
