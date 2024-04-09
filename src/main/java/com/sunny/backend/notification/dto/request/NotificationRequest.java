package com.sunny.backend.notification.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotificationRequest {

	@NotBlank(message = "디바이스 토큰 값은 필수 값입니다.")
	private String targetToken;

	@Getter
	@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class NotificationAllowRequest {
		@NotNull(message = "허용 여부 값은 필수 값입니다.")
		private boolean allow;
		private String targetToken;
	}
}
