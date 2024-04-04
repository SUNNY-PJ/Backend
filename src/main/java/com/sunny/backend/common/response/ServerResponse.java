package com.sunny.backend.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ServerResponse<T>(
	HttpStatus code,
	T data
) {
	public static <T> ResponseEntity<ServerResponse<T>> ok(T data) {
		return ResponseEntity.ok(new ServerResponse<>(HttpStatus.OK, data));
	}

}
