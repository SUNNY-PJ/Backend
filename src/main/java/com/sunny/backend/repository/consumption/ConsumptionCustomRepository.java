package com.sunny.backend.repository.consumption;

import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.entity.Consumption;

import java.time.LocalDate;
import java.util.List;

public interface ConsumptionCustomRepository {
    List<SpendTypeStatisticsResponse> getSpendTypeStatistics();
    Long getComsumptionMoney(Long id, LocalDate startDate, LocalDate endDate);
}
