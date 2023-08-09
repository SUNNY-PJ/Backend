package com.sunny.backend.repository.consumption;

import com.sunny.backend.dto.response.ConsumptionResponse;
import com.sunny.backend.dto.response.SpendTypeStatisticsResponse;

import java.util.List;

public interface ConsumptionCustomRepository {
    List<SpendTypeStatisticsResponse> getSpendTypeStatistics();

}
