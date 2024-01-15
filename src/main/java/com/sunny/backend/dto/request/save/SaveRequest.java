package com.sunny.backend.dto.request.save;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import lombok.Getter;

import java.util.Date;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SaveRequest {

    private Long cost;
    private String saveContent;
    @JsonFormat(pattern = "yyyy.MM.dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy.MM.dd")
    private LocalDate endDate;
}

