package com.sunny.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.Date;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConsumptionRequest {

    private String name; //지출명
    private String place; //지출 장소
    private Long money; //지출 금액

    private Date dateField; // 지출 일자

}
