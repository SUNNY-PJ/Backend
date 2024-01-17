package com.sunny.backend.friends.exception;

import org.springframework.http.HttpStatus;
import com.sunny.backend.common.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FriendErrorCode implements ErrorCode {
	FRIEND_NOT_APPROVE(HttpStatus.BAD_REQUEST, "이미 친구 신청을 했고, 친구 승인 대기 상태입니다."),
	FRIEND_EXIST(HttpStatus.CONFLICT, "이미 친구입니다."),
	FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "친구가 아닙니다.");

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
