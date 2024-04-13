package com.sunny.backend.competition.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.response.CompetitionStatusResponse;
import com.sunny.backend.competition.service.CompetitionService;
import com.sunny.backend.friends.dto.response.FriendCompetitionResponses;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "3. Competition", description = "Competition API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/competition")
public class CompetitionController {
	private final CompetitionService competitionService;

	@ApiOperation(tags = "3. Competition", value = "대결 목록 확인")
	@GetMapping("/{friendId}")
	public ResponseEntity<CommonResponse.ListResponse<FriendCompetitionResponses>> getCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId,
		@RequestParam(name = "competitionId", required = false) Long competitionId
	) {
		return competitionService.getCompetition(customUserPrincipal, friendId, competitionId);
		// List<FriendCompetitionResponses> responses = competitionService.getCompetition(customUserPrincipal, friendId,
		// 	competitionId);
		// return ResponseEntity.ok(responses);
	}

	@ApiOperation(tags = "3. Competition", value = "친구에게 대결 신청")
	@PostMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<FriendCompetitionResponses>> applyCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody CompetitionRequest competitionRequest
	) {
		return competitionService.applyCompetition(customUserPrincipal, competitionRequest);
		// FriendCompetitionResponses response = competitionService.applyCompetition(customUserPrincipal,
		// 	competitionRequest);
		// return ResponseEntity.ok(response);
	}

	@ApiOperation(tags = "3. Competition", value = "대결 승인하기")
	@PostMapping("/approve/{friendId}")
	public ResponseEntity<Void> acceptCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		competitionService.acceptCompetition(customUserPrincipal, friendId);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(tags = "3. Competition", value = "대결 거절하기")
	@DeleteMapping("/approve/{friendId}")
	public ResponseEntity<Void> refuseCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		competitionService.refuseFriend(customUserPrincipal, friendId);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(tags = "3. Competition", value = "대결 상태 가져오기")
	@GetMapping("/status/{friendId}")
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionStatusResponse>> getCompetitionStatus(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId,
		@RequestParam(name = "competitionId") Long competitionId
	) {
		return competitionService.getCompetitionStatus(customUserPrincipal, friendId, competitionId);
		// CompetitionStatusResponse response = competitionService.getCompetitionStatus(customUserPrincipal, friendId,
		// 	competitionId);
		// return ResponseEntity.ok().body(response);
	}

	@ApiOperation(tags = "3. Competition", value = "대결 포기하기")
	@DeleteMapping("/give-up/{friendId}")
	public ResponseEntity<Void> giveUpCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "friendId") Long friendId) {
		competitionService.giveUpCompetition(customUserPrincipal, friendId);
		return ResponseEntity.ok().build();
	}
}
