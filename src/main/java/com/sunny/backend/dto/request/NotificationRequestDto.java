package com.sunny.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotificationRequestDto {
    @NotBlank
    private String targetToken;

}
