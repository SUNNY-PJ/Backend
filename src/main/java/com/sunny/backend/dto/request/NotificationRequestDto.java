package com.sunny.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotificationRequestDto {
    @NotBlank(message = "디바이스 토큰 값은 필수 값입니다.")
    private String targetToken;

}
