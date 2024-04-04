package com.sunny.backend.common.response;

import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

public record ErrorResponse(
	HttpStatus code,
	String errorMessage
) {
	public static ErrorResponse from(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.getHttpStatus(), errorCode.getMessage());
	}
}
