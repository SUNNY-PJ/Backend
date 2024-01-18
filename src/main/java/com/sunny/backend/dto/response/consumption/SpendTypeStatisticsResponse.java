package com.sunny.backend.dto.response.consumption;

import com.sunny.backend.consumption.domain.SpendType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpendTypeStatisticsResponse {
    private SpendType category;
    private long totalCount;
    private long totalMoney;
    private double percentage;

}
