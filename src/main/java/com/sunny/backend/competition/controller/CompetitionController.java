package com.sunny.backend.competition.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.common.response.ServerResponse;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.response.CompetitionResponse;
import com.sunny.backend.competition.dto.response.CompetitionStatusResponse;
import com.sunny.backend.competition.service.CompetitionService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "3. Competition", description = "Competition API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/competition")
public class CompetitionController {

	private final ResponseService responseService;
	private final CompetitionService competitionService;

	@ApiOperation(tags = "3. Competition", value = "대결 목록 확인")
	@GetMapping("/{friendId}")
	public ResponseEntity<ServerResponse<CompetitionResponse>> getCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		CompetitionResponse response = competitionService.getCompetition(customUserPrincipal, friendId);
		return ServerResponse.ok(response, "대결 목록 확인 성공");
	}

	@ApiOperation(tags = "3. Competition", value = "친구에게 대결 신청")
	@PostMapping("")
	public ResponseEntity<ServerResponse<CompetitionResponse>> applyCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody CompetitionRequest competitionRequest) {
		CompetitionResponse response = competitionService.applyCompetition(customUserPrincipal, competitionRequest);
		return ServerResponse.ok(response, "친구에게 대결 신청이 성공했습니다.");
	}

	@ApiOperation(tags = "3. Competition", value = "대결 승인하기")
	@PostMapping("/approve/{friendId}")
	public ResponseEntity<ServerResponse<Void>> acceptCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		competitionService.acceptCompetition(customUserPrincipal, friendId);
		return ServerResponse.message("대결 신청을 승인했습니다.");
	}

	@ApiOperation(tags = "3. Competition", value = "대결 거절하기")
	@DeleteMapping("/approve/{friendId}")
	public ResponseEntity<ServerResponse<Void>> refuseCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		competitionService.refuseFriend(customUserPrincipal, friendId);
		return ServerResponse.message("대결 신청을 거절했습니다.");
	}

	@ApiOperation(tags = "3. Competition", value = "대결 상태 가져오기")
	@GetMapping("/status/{friendId}")
	public ResponseEntity<ServerResponse<CompetitionStatusResponse>> getCompetitionStatus(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "friendId") Long friendId) {
		CompetitionStatusResponse response = competitionService.getCompetitionStatus(customUserPrincipal, friendId);
		return ServerResponse.ok(response, "현재 대결 중인 상태를 가져왔습니다.");
	}

	@ApiOperation(tags = "3. Competition", value = "대결 포기하기")
	@DeleteMapping("/give-up/{friendId}")
	public ResponseEntity<Void> giveUpCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "friendId") Long friendId) {
		competitionService.giveUpCompetition(customUserPrincipal, friendId);
		return ResponseEntity.ok().build();
	}
}
