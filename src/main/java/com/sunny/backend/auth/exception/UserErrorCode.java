package com.sunny.backend.auth.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	NICKNAME_IN_USE(HttpStatus.FORBIDDEN, "이미 사용중인 닉네임입니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
