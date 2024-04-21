package com.sunny.backend.user.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BlockListErrorCode implements ErrorCode {
	BLOCK_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "차단한 사용자가 존재하지 않습니다."),
	SELF_BLOCK_ERROR(HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수 없습니다.");
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
