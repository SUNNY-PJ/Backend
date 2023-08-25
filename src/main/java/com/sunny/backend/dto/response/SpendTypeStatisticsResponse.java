package com.sunny.backend.dto.response;

import com.sunny.backend.entity.SpendType;
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
