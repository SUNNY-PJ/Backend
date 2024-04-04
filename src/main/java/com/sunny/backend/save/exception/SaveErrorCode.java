package com.sunny.backend.save.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SaveErrorCode implements ErrorCode {
	SAVE_NOT_FOUND(HttpStatus.NOT_FOUND, "절약 목표가 존재하지 않습니다."),
	ALREADY_SAVE(HttpStatus.BAD_REQUEST, "이미 등록된 절약 목표가 존재합니다.");

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
