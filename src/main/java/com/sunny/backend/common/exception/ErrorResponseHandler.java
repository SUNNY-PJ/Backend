package com.sunny.backend.common.exception;

import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseHandler {
	private int status;
	private String message;

	public static ResponseEntity<ErrorResponseHandler> toResponseEntity(ErrorCode e) {
		return ResponseEntity
			.status(e.getHttpStatus())
			.body(ErrorResponseHandler.builder()
				.status(e.getHttpStatus().value())
				.message(e.getMessage())
				.build()
			);
	}
}