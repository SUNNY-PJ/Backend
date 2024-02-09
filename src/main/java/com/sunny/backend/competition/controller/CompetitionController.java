package com.sunny.backend.competition.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
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
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.response.CompetitionApplyResponse;
import com.sunny.backend.competition.dto.response.CompetitionResponseDto;
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

	@ApiOperation(tags = "3. Competition", value = "친구에게 대결 신청")
	@PostMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionApplyResponse>> applyCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody CompetitionRequest competitionRequest) throws IOException {
		return competitionService.applyCompetition(customUserPrincipal, competitionRequest);
	}

	@ApiOperation(tags = "3. Competition", value = "대결 승인하기")
	@PostMapping("/approve/{friendId}")
	public ResponseEntity<CommonResponse.GeneralResponse> acceptCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		competitionService.acceptCompetition(customUserPrincipal, friendId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "승인 되었습니다.");
	}

	@ApiOperation(tags = "3. Competition", value = "대결 거절하기")
	@DeleteMapping("/approve/{friendId}")
	public ResponseEntity<CommonResponse.GeneralResponse> refuseCompetition(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "friendId") Long friendId) {
		competitionService.refuseFriend(customUserPrincipal, friendId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "거절 되었습니다.");
	}

	@ApiOperation(tags = "3. Competition", value = "대결 상태 가져오기")
	@GetMapping("/status/{friendId}")
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponseDto.CompetitionStatus>> getCompetitionStatus(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "friendId") Long friendId) {
		return competitionService.getCompetitionStatus(customUserPrincipal, friendId);
	}
}
