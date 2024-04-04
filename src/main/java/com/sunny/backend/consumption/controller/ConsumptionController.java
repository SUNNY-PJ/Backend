package com.sunny.backend.consumption.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ServerResponse;
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

	@ApiOperation(tags = "4. Consumption", value = "지출 조회")
	@GetMapping("")
	public ResponseEntity<ServerResponse<List<ConsumptionResponse>>> getConsumptionList(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		List<ConsumptionResponse> response = consumptionService.getConsumptionList(customUserPrincipal);
		return ServerResponse.ok(response, "지출 내역을 불러왔습니다.");
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 등록")
	@PostMapping("")
	public ResponseEntity<ServerResponse<ConsumptionResponse>> createConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody ConsumptionRequest consumptionRequest) {
		ConsumptionResponse response = consumptionService.createConsumption(customUserPrincipal, consumptionRequest);
		return ServerResponse.ok(response, "지출을 등록했습니다.");
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 통계")
	@GetMapping("/spendTypeStatistics")
	public ResponseEntity<ServerResponse<List<SpendTypeStatisticsResponse>>> getSpendTypeStatistics(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam(name = "year") Integer year,
		@RequestParam(name = "month") Integer month
	) {
		List<SpendTypeStatisticsResponse> response = consumptionService.getSpendTypeStatistics(customUserPrincipal,
			year, month);
		return ServerResponse.ok(response, "지출 통계 내역을 불러왔습니다.");
	}

	@ApiOperation(tags = "4. Consumption", value = "날짜에 맞는 지출 내역 조회")
	@GetMapping("/date")
	public ResponseEntity<ServerResponse<List<ConsumptionResponse.DetailConsumptionResponse>>> getDetailConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam("datefield") @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate dateField) {
		List<ConsumptionResponse.DetailConsumptionResponse> response = consumptionService.getDetailConsumption(
			customUserPrincipal, dateField);
		return ServerResponse.ok(response, dateField + "에 맞는 지출 내역을 불러왔습니다.");
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 내역 수정")
	@PatchMapping("/{consumptionId}")
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> updateConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestBody ConsumptionRequest consumtionRequest,
		@PathVariable Long consumptionId) {
		return consumptionService.updateConsumption(customUserPrincipal, consumtionRequest,
			consumptionId);
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 내역 삭제")
	@DeleteMapping("/{consumptionId}")
	public ResponseEntity<Void> deleteConsumption(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable Long consumptionId) {
		consumptionService.deleteConsumption(customUserPrincipal, consumptionId);
		return ResponseEntity.noContent().build();
		// "지출 내역을 삭제했습니다.");
	}

	@ApiOperation(tags = "4. Consumption", value = "카테고리별 지출 내역 조회")
	@GetMapping("/category")
	public ResponseEntity<ServerResponse<List<DetailConsumptionResponse>>> getConsumptionByCategory(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@Valid @RequestParam SpendType spendType,
		@RequestParam(name = "year") Integer year, @RequestParam(name = "month") Integer month) {
		List<DetailConsumptionResponse> response = consumptionService.getConsumptionByCategory(customUserPrincipal,
			spendType, year, month);
		return ServerResponse.ok(response, spendType + "에 맞는 지출 내역을 불러왔습니다.");
	}
}
