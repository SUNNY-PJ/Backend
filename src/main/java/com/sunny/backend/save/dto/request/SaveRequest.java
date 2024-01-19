package com.sunny.backend.save.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Date;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SaveRequest {

    @NotNull(message = "지출 금액은 필수 입력값입니다.")
    private Long cost;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @NotNull(message = "시작 날짜는 필수 입력값입니다.")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy.MM.dd")
    @NotNull(message = "종료 날짜는 필수 입력값입니다.")

    private LocalDate endDate;
}

