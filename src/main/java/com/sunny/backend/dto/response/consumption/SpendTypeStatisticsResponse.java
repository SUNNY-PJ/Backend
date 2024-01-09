package com.sunny.backend.dto.response.consumption;

import com.sunny.backend.entity.SpendType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpendTypeStatisticsResponse {
    private SpendType category;
    private long totalCount;
    private long totalMoney;
    private long percentage;

}
