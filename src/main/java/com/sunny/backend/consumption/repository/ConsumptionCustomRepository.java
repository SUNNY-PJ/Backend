package com.sunny.backend.consumption.repository;

import java.time.LocalDate;
import java.util.List;

import com.sunny.backend.consumption.domain.SpendType;
import com.sunny.backend.consumption.dto.response.ConsumptionResponse;
import com.sunny.backend.consumption.dto.response.SpendTypeStatisticsResponse;

public interface ConsumptionCustomRepository {

	List<SpendTypeStatisticsResponse> getSpendTypeStatistics(Long userId, Integer year, Integer month);

	List<ConsumptionResponse.DetailConsumptionResponse> getConsumptionByCategory(Long userId,
		SpendType spendType, Integer year, Integer month);

	Long getComsumptionMoney(Long id, LocalDate startDate, LocalDate endDate);
}
