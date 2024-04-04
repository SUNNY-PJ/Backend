package com.sunny.backend.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ServerResponse<T>(
	HttpStatus code,
	T data,
	String msg
) {
	public static <T> ResponseEntity<ServerResponse<T>> ok(T data, String msg) {
		return ResponseEntity.ok(new ServerResponse<>(HttpStatus.OK, data, msg));
	}

	public static ResponseEntity<ServerResponse<Void>> message(String msg) {
		return ResponseEntity.ok(new ServerResponse<>(HttpStatus.OK, null, msg));
	}
}
