package com.sunny.backend.competition.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CompetitionErrorCode implements ErrorCode {
	COMPETITION_NOT_MYSELF(HttpStatus.FORBIDDEN, "신청한 사람은 해당 권한이 없습니다."),
	COMPETITION_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러로 대결을 삭제합니다."),
	COMPETITION_NOT_RECEIVE(HttpStatus.BAD_REQUEST, "대결 신청을 받지 않았습니다"),
	COMPETITION_NOT_FOUND(HttpStatus.NOT_FOUND, "대결중인 친구를 찾을 수 없습니다."),
	COMPETITION_NOT(HttpStatus.BAD_REQUEST, "친구와 대결하고 있지 않습니다."),
	COMPETITION_SEND(HttpStatus.BAD_REQUEST, "이미 대결 신청을 했고, 대결 승인 대기 상태입니다."),
	COMPETITION_RECEIVE(HttpStatus.BAD_REQUEST, "이미 대결 신청을 받아서 신청할 수 없습니다."),
	COMPETITION_EXIST(HttpStatus.BAD_REQUEST, "이미 대결중입니다.");

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
