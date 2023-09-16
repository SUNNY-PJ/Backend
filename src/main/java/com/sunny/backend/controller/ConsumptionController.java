package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.consumption.ConsumptionRequest;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.consumption.ConsumptionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="4. Consumption", description = "Consumption API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/consumption")
public class ConsumptionController {
    private final ConsumptionService consumptionService;

    //지출 조회
    @ApiOperation(tags = "4. Consumption", value = "지출 조회")
    @GetMapping("")
    public ResponseEntity<CommonResponse.ListResponse> getConsumptionList(@AuthUser CustomUserPrincipal customUserPrincipal){
        return ResponseEntity.ok().body(consumptionService.getConsumptionList(customUserPrincipal));
    }

    @ApiOperation(tags = "4. Consumption", value = "지출 등록")
    @PostMapping("")
    public ResponseEntity<CommonResponse.SingleResponse> createConsumption(@AuthUser CustomUserPrincipal customUserPrincipal, @RequestBody ConsumptionRequest consumtionRequest ) {

        return ResponseEntity.ok().body(consumptionService.createConsumption(customUserPrincipal,consumtionRequest));
    }

    @ApiOperation(tags = "4. Consumption", value = "지출 통계")
    //지출 통계
    @GetMapping("/spendTypeStatistics")
    public ResponseEntity<CommonResponse.ListResponse> getSpendTypeStatistics() {
        return ResponseEntity.ok().body(consumptionService.getSpendTypeStatistics());
    }
}
