package com.sunny.backend.community.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommunityErrorCode implements ErrorCode {
	COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 글을 찾을 수 없습니다.");

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
