package com.sunny.backend.consumption.repository;

import com.sunny.backend.consumption.domain.SpendType;
import com.sunny.backend.consumption.dto.request.YearMonthRequest;
import com.sunny.backend.consumption.dto.response.ConsumptionResponse;
import com.sunny.backend.consumption.dto.response.SpendTypeStatisticsResponse;

import java.time.LocalDate;
import java.util.List;

public interface ConsumptionCustomRepository {

    List<SpendTypeStatisticsResponse> getSpendTypeStatistics(Long userId,Integer year,Integer month);
    List<ConsumptionResponse.DetailConsumptionResponse> getConsumptionByCategory(Long userId,
        SpendType spendType, Integer year,Integer month);

    Long getComsumptionMoney(Long id, LocalDate startDate, LocalDate endDate);
}
