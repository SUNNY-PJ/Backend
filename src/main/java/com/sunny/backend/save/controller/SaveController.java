package com.sunny.backend.save.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.save.dto.request.SaveRequest;
import com.sunny.backend.save.dto.response.DetailSaveResponse;
import com.sunny.backend.save.dto.response.SaveResponse;
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
	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> createSaveGoal(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody SaveRequest saveRequest) {
		Long id = saveService.createSaveGoal(customUserPrincipal, saveRequest);
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path("/{id}")
			.buildAndExpand(id).toUri();
		return ResponseEntity.created(uri)
			.build();
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 수정")
	@PutMapping
	public ResponseEntity<Void> updateSaveGoal(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody SaveRequest saveRequest) {
		saveService.updateSaveGoal(customUserPrincipal, saveRequest);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 조회")
	@GetMapping
	public ResponseEntity<List<DetailSaveResponse>> getSaveGoal(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		List<DetailSaveResponse> responses = saveService.getSaveGoal(customUserPrincipal);
		return ResponseEntity.ok(responses);
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 세부 조회")
	@GetMapping("/detail")
	public ResponseEntity<CommonResponse.ListResponse<SaveResponse>> getDetailSaveGaol(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		return saveService.getDetailSaveGoal(customUserPrincipal);
	}

	@ApiOperation(tags = "6. Save", value = "절약 목표 세부 조회")
	@GetMapping("/{id}")
	public ResponseEntity<SaveResponse> getDetailSaveGaol(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "id") Long id) {
		SaveResponse response = saveService.getDetailSave(customUserPrincipal, id);
		return ResponseEntity.ok(response);
	}

}
