package com.sunny.backend.scrap.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ScrapErrorCode implements ErrorCode {
	SCRAP_ALREADY(HttpStatus.BAD_REQUEST, "이미 스크랩한 게시글입니다."),
	SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "스크랩 게시글을 찾을 수 없습니다.");

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

