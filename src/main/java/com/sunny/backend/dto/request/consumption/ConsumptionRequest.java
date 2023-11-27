package com.sunny.backend.dto.request.consumption;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConsumptionRequest {

    private String name; //지출명
    private String category; //지출 장소
    private Long money; //지출 금액

    private LocalDate dateField; // 지출 일자

}





