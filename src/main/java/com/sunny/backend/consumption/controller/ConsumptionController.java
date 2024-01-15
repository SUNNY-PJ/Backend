package com.sunny.backend.consumption.controller;


import com.sunny.backend.dto.response.consumption.ConsumptionResponse.DetailConsumptionResponse;
import com.sunny.backend.consumption.domain.SpendType;
import java.time.LocalDate;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.consumption.ConsumptionRequest;
import com.sunny.backend.dto.response.consumption.ConsumptionResponse;
import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
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
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse>> getConsumptionList(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		return consumptionService.getConsumptionList(customUserPrincipal);
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 등록")
	@PostMapping("")
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> createConsumption(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@Valid @RequestBody ConsumptionRequest consumtionRequest) {
		return consumptionService.createConsumption(customUserPrincipal, consumtionRequest);
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 통계")
	@GetMapping("/spendTypeStatistics")
	public ResponseEntity<CommonResponse.ListResponse<SpendTypeStatisticsResponse>>
	getSpendTypeStatistics(@AuthUser CustomUserPrincipal customUserPrincipal) {
		return consumptionService.getSpendTypeStatistics(customUserPrincipal);
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
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> updateConsumption(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@Valid @RequestBody ConsumptionRequest consumtionRequest,
			@PathVariable Long consumptionId) {
		return consumptionService.updateConsumption(customUserPrincipal, consumtionRequest,
				consumptionId);
	}

	@ApiOperation(tags = "4. Consumption", value = "지출 내역 삭제")
	@DeleteMapping("/{consumptionId}")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteConsumption(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@PathVariable Long consumptionId) {
		return consumptionService.deleteConsumption(customUserPrincipal, consumptionId);
	}

	@ApiOperation(tags = "4. Consumption", value = "카테고리별 지출 내역 조회")
	@GetMapping("/category")
	public ResponseEntity<CommonResponse.ListResponse<DetailConsumptionResponse>> getConsumptionByCategory(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@Valid @RequestParam(required = false) SpendType spendType) {
		return consumptionService.getConsumptionByCategory(customUserPrincipal, spendType);
	}
}
