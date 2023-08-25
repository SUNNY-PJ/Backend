package com.sunny.backend.controller;

import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.ConsumptionRequest;
import com.sunny.backend.dto.response.SpendTypeStatisticsResponse;
import com.sunny.backend.service.consumption.ConsumptionService;
import com.sunny.backend.user.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import java.io.IOException;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/consumption")
public class ConsumptionController {
    private final ConsumptionService consumptionService;

    @GetMapping("")
    //Auth user만 추가
    public ResponseEntity getConsumptionLists(@ApiIgnore @AuthUser Users users) throws IOException  {

        return consumptionService.getConsumptionList(users);
    }
    @PostMapping("")
    public ResponseEntity createConsumption(@ApiIgnore @AuthUser Users users, @RequestBody ConsumptionRequest consumtionRequest ) throws IOException {

        return consumptionService.createConsumption(users,consumtionRequest);
    }

    @GetMapping("/spendTypeStatistics")
    public ResponseEntity<List<SpendTypeStatisticsResponse>> getSpendTypeStatistics() {
        List<SpendTypeStatisticsResponse> statistics = consumptionService.getSpendTypeStatistics();
        return ResponseEntity.ok(statistics);
    }
}
