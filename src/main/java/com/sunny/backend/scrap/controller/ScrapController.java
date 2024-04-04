package com.sunny.backend.scrap.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
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
	public ResponseEntity<Void> addScrapToCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long communityId
	) {
		scrapService.addScrapToCommunity(customUserPrincipal, communityId);
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path("/community/{id}")
			.buildAndExpand(communityId).toUri();
		return ResponseEntity.created(uri).build();
	}

	@ApiOperation(tags = "2.1 Scrap", value = "스크랩 삭제")
	@DeleteMapping("/{communityId}")
	public ResponseEntity<Void> removeScrapFromCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long communityId
	) {
		scrapService.removeScrapFromCommunity(customUserPrincipal, communityId);
		return ResponseEntity.noContent().build();
	}
}
