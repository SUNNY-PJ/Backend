package com.sunny.backend.save.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.save.dto.request.SaveRequest;
import com.sunny.backend.save.dto.response.DetailSaveResponse;
import com.sunny.backend.save.dto.response.SaveResponse;
import com.sunny.backend.save.dto.response.SaveResponses;
import com.sunny.backend.save.service.SaveService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "6. Save", description = "Save API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/save")
public class SaveController {
	private final SaveService saveService;

	@ApiOperation(tags = "6. Save", value = "절약 목표 등록")
	@PostMapping
	public ResponseEntity<SaveResponse> createSaveGoal(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody SaveRequest saveRequest
	) {
		SaveResponse response = saveService.createSaveGoal(customUserPrincipal, saveRequest);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 수정")
	@PatchMapping
	public ResponseEntity<SaveResponse> updateSaveGoal(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody SaveRequest saveRequest
	) {
		SaveResponse response = saveService.updateSaveGoal(customUserPrincipal, saveRequest);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 조회")
	@GetMapping
	public ResponseEntity<List<DetailSaveResponse>> getSaveGaol(
		@AuthUser CustomUserPrincipal customUserPrincipal
	) {
		List<DetailSaveResponse> responses = saveService.getSaveGoal(customUserPrincipal);
		return ResponseEntity.ok(responses);
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 세부 조회")
	@GetMapping("/detail")
	public ResponseEntity<List<SaveResponses>> getDetailSaveGaol(
		@AuthUser CustomUserPrincipal customUserPrincipal
	) {
		List<SaveResponses> responses = saveService.getDetailSaveGoal(customUserPrincipal);
		return ResponseEntity.ok(responses);
	}

}
