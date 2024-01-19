package com.sunny.backend.consumption.repository;

import com.sunny.backend.consumption.dto.response.SpendTypeStatisticsResponse;

import java.time.LocalDate;
import java.util.List;

public interface ConsumptionCustomRepository {

    List<SpendTypeStatisticsResponse> getSpendTypeStatistics(Long userId);

    Long getComsumptionMoney(Long id, LocalDate startDate, LocalDate endDate);
}
