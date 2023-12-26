package com.sunny.backend.dto.request.consumption;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sunny.backend.entity.SpendType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConsumptionRequest {
    private String name; //지출명
    private SpendType category; //지출 장소
    private Long money; //지출 금액
    private LocalDate dateField; // 지출 일자
}
