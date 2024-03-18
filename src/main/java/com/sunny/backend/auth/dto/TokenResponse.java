package com.sunny.backend.auth.dto;

public record TokenResponse (
	String accessToken,
	String refreshToken,
	boolean isUserRegistered
){
}
