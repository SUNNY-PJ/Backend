package com.sunny.backend.repository.consumption;

import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;

import java.util.List;

public interface ConsumptionCustomRepository {
    List<SpendTypeStatisticsResponse> getSpendTypeStatistics();

}
