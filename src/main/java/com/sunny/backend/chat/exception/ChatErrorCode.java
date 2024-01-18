package com.sunny.backend.chat.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {
	CHAT_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "대화 상대가 존재하지 않습니다.");


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
