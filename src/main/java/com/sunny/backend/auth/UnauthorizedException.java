package com.sunny.backend.auth;

public class UnauthorizedException extends RuntimeException {
	public UnauthorizedException(String message) {
		super(message);
	}

	public static UnauthorizedException invalid() {
		return new UnauthorizedException("Invalid authorization token");
	}
}