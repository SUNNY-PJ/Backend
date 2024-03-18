package com.sunny.backend.consumption.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sunny.backend.consumption.domain.SpendType;
import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConsumptionRequest {
    @NotBlank(message = "지출 이름은 필수 입력값입니다.")
    @Size(max = 30, message = "최대 글자 수는 30글자입니다.")
    private String name;
    @NotNull(message = "올바른 카테고리 값을 입력해야합니다.")
    private SpendType category;
    @PositiveOrZero
    @NotNull(message = "지출 금액은 필수 입력값입니다.")
    private Long money;
    @NotNull(message = "지출 날짜는 필수 입력값입니다.")
    @JsonFormat(pattern = "yyyy.MM.dd")
    private LocalDate dateField;
}
