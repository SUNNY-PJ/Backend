package com.sunny.backend.save.controller;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.save.SaveRequest;
import com.sunny.backend.dto.response.save.SaveResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
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
	@PostMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> createSaveGoal(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@Valid @RequestBody SaveRequest saveRequest) {
		return saveService.createSaveGoal(customUserPrincipal, saveRequest);
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 수정")
	@PutMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> updateSaveGoal(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@Valid @RequestBody SaveRequest saveRequest) {
		return saveService.updateSaveGoal(customUserPrincipal, saveRequest);
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 조회")
	@GetMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> getSaveGaol(
			@AuthUser CustomUserPrincipal customUserPrincipal) {
		return saveService.getSaveGoal(customUserPrincipal);
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 세부 조회")
	@GetMapping("/detail")
	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse.DetailSaveResponse>> getDetailSaveGaol(
			@AuthUser CustomUserPrincipal customUserPrincipal) {
		return saveService.getDetailSaveGoal(customUserPrincipal);
	}
}
