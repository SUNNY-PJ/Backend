package com.sunny.backend.report.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReportErrorCode implements ErrorCode {
	ALREADY_PROCESS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 처리된 신고 기록 입니다."),
	REPORT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 신고 기록을 찾을 수 없습니다."),
	REPORT_COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 신고 기록을 찾을 수 없습니다."),
	INVALID_REPORT_TYPE(HttpStatus.INTERNAL_SERVER_ERROR, "허용하지 않는 신고 유형입니다.");

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

