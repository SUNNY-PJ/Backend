package com.sunny.backend.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OAuthToken {
	private String idToken;
	private String accessToken;
	private String tokenType;
	private String refreshToken;
	private Integer expiresIn;
	private String scope;
	private Integer refreshTokenExpiresIn;
}
