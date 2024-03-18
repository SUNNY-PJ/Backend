package com.sunny.backend.notification.exception;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
	NOTIFICATIONS_NOT_SENT(HttpStatus.NOT_FOUND, "해당 사용자에게 알림을 보낼 수 없습니다.");

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

