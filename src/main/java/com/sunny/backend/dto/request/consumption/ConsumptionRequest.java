package com.sunny.backend.dto.request.consumption;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sunny.backend.entity.SpendType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConsumptionRequest {

    private String name;
    private SpendType category;
    private Long money;
    private LocalDate dateField;
}
