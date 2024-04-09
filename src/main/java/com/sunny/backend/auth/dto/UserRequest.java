package com.sunny.backend.auth.dto;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRequest {
	@NotEmpty(message = "잘못된 요청입니다.")
	private String accessToken;

	@NotEmpty(message = "잘못된 요청입니다.")
	private String refreshToken;
}
