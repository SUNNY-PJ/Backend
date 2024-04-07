package com.sunny.backend.community.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommunityErrorCode implements ErrorCode {
	COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 글을 찾을 수 없습니다."),
	ALREADY_SCRAPED(HttpStatus.BAD_REQUEST, "이미 스크랩한 커뮤니티입니다.");
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

