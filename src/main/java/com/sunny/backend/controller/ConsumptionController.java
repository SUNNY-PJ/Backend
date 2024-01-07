package com.sunny.backend.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.consumption.ConsumptionRequest;
import com.sunny.backend.dto.response.consumption.ConsumptionResponse;
import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.consumption.ConsumptionService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "4. Consumption", description = "Consumption API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/consumption")
public class ConsumptionController {
	private final ConsumptionService consumptionService;

	//지출 조회
	@ApiOperation(tags = "4. Consumption", value = "지출 조회")
	@GetMapping("")
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse>> getConsumptionList(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		return consumptionService.getConsumptionList(customUserPrincipal);
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 등록")
	@PostMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> createConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal, @RequestBody ConsumptionRequest consumtionRequest) {

		return consumptionService.createConsumption(customUserPrincipal, consumtionRequest);
	}

	//지출 통계
	@ApiOperation(tags = "4. Consumption", value = "지출 통계")
	@GetMapping("/spendTypeStatistics")
	public ResponseEntity<CommonResponse.ListResponse<SpendTypeStatisticsResponse>> getSpendTypeStatistics() {
		return consumptionService.getSpendTypeStatistics();
	}

	//지출 내역
	@ApiOperation(tags = "4. Consumption", value = "날짜에 맞는 지출 내역 조회")
	@GetMapping("/date")
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse.DetailConsumption>> getDetailConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam("datefield") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate datefield) {
		return consumptionService.getDetailConsumption(customUserPrincipal, datefield);
	}

	//지출 내역
	@ApiOperation(tags = "4. Consumption", value = "지출 내역 수정")
	@PutMapping("/{consumptionId}")
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> updateConsumption(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@RequestBody ConsumptionRequest consumtionRequest,
			@PathVariable Long consumptionId) {
		return consumptionService.updateConsumption(customUserPrincipal, consumtionRequest,consumptionId);
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 내역 삭제")
	@PutMapping("/{consumptionId}")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteConsumption(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@PathVariable Long consumptionId) {
		return consumptionService.deleteConsumption(customUserPrincipal,consumptionId);
	}
}
