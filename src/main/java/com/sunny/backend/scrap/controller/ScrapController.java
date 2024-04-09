package com.sunny.backend.scrap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.scrap.service.ScrapService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "2.1 Scrap", description = "Scrap API")
@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
public class ScrapController {
	private final ScrapService scrapService;

	@ApiOperation(tags = "2.1 Scrap", value = "스크랩 등록")
	@PostMapping("/{communityId}")
	public ResponseEntity<CommonResponse.GeneralResponse> addScrapToCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		return scrapService.addScrapToCommunity(customUserPrincipal, communityId);
	}

	@ApiOperation(tags = "2.1 Scrap", value = "스크랩 삭제")
	@DeleteMapping("/{communityId}")
	public ResponseEntity<CommonResponse.GeneralResponse> removeScrapFromCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		return scrapService.removeScrapFromCommunity(customUserPrincipal, communityId);
	}
}
