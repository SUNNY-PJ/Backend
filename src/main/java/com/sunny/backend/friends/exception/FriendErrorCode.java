package com.sunny.backend.friends.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FriendErrorCode implements ErrorCode {
	FRIEND_RECEIVE(HttpStatus.INTERNAL_SERVER_ERROR, "이미 친구 신청을 받았고, 친구 승인 대기 상태입니다."),
	FRIEND_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "이미 친구 신청을 했고, 친구 승인 대기 상태입니다."),
	ALREADY_FRIEND(HttpStatus.CONFLICT, "이미 친구입니다."),
	FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "친구가 아닙니다."),
	FRIEND_NOT_MYSELF(HttpStatus.INTERNAL_SERVER_ERROR, "자신에게 친구 신청을 보낼 수 없습니다."),
	FRIEND_NOT_COMPETITION(HttpStatus.INTERNAL_SERVER_ERROR, "해당 친구와 경쟁하고 있지 않습니다."),
	FRIEND_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러");

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
