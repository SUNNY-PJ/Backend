package com.sunny.backend.consumption.controller;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.competition.service.CompetitionScheduleService;
import com.sunny.backend.consumption.domain.SpendType;
import com.sunny.backend.consumption.dto.request.ConsumptionRequest;
import com.sunny.backend.consumption.dto.response.ConsumptionResponse;
import com.sunny.backend.consumption.dto.response.ConsumptionResponse.DetailConsumptionResponse;
import com.sunny.backend.consumption.dto.response.SpendTypeStatisticsResponse;
import com.sunny.backend.consumption.service.ConsumptionService;

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
	private final CompetitionScheduleService competitionScheduleService;

	@ApiOperation(tags = "4. Consumption", value = "지출 조회")
	@GetMapping
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse>> getConsumptionList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(required = false) Long consumptionId
	) {
		return consumptionService.getConsumptionList(customUserPrincipal, consumptionId);
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 등록")
	@PostMapping
	public ResponseEntity<ConsumptionResponse> createConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody ConsumptionRequest consumptionRequest
	) {
		consumptionService.createConsumption(customUserPrincipal, consumptionRequest);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 통계")
	@GetMapping("/spendTypeStatistics")
	public ResponseEntity<CommonResponse.ListResponse<SpendTypeStatisticsResponse>> getSpendTypeStatistics(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(name = "year") Integer year, @RequestParam(name = "month") Integer month) {
		return consumptionService.getSpendTypeStatistics(customUserPrincipal, year, month);
	}

	@ApiOperation(tags = "4. Consumption", value = "날짜에 맞는 지출 내역 조회")
	@GetMapping("/date")
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse.DetailConsumptionResponse>> getDetailConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam("datefield") @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate datefield) {
		return consumptionService.getDetailConsumption(customUserPrincipal, datefield);
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 내역 수정")
	@PutMapping("/{consumptionId}")
	public ResponseEntity<Void> updateConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody ConsumptionRequest consumtionRequest,
		@PathVariable Long consumptionId) {
		consumptionService.updateConsumption(customUserPrincipal, consumtionRequest, consumptionId);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 내역 삭제")
	@DeleteMapping("/{consumptionId}")
	public ResponseEntity<Void> deleteConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long consumptionId) {
		consumptionService.deleteConsumption(customUserPrincipal, consumptionId);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(tags = "4. Consumption", value = "카테고리별 지출 내역 조회")
	@GetMapping("/category")
	public ResponseEntity<CommonResponse.ListResponse<DetailConsumptionResponse>> getConsumptionByCategory(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestParam SpendType spendType,
		@RequestParam(name = "year") Integer year, @RequestParam(name = "month") Integer month) {
		return consumptionService.getConsumptionByCategory(customUserPrincipal, spendType, year, month);
	}

	@GetMapping("/test")
	public void test() {
		System.out.println("test api 호출");
		competitionScheduleService.checkCompetition();
	}
}
