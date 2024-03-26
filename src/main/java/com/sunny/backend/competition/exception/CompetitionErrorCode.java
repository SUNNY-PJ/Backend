package com.sunny.backend.competition.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CompetitionErrorCode implements ErrorCode {
	COMPETITION_SEND(HttpStatus.BAD_REQUEST, "이미 대결 신청을 했고, 대결 승인 대기 상태입니다."),
	COMPETITION_EXIST(HttpStatus.CONFLICT, "이미 대결중입니다.");

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
