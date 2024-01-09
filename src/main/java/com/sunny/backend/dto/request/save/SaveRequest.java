package com.sunny.backend.dto.request.save;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.Date;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SaveRequest {

    private Long cost;
    private String saveContent;
    private String startDate;
    private String endDate;
}

