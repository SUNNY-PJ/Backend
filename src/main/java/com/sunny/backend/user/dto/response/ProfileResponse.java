package com.sunny.backend.user.dto.response;

import com.sunny.backend.user.domain.Users;

public record ProfileResponse(
	Long id,
	String name,
	String profile,
	boolean owner
) {
	public static ProfileResponse from(Users users) {
		return new ProfileResponse(
			users.getId(),
			users.getNickname(),
			users.getProfile(),
			true
		);
	}

	public static ProfileResponse of(Users users, boolean owner) {
		return new ProfileResponse(
			users.getId(),
			users.getNickname(),
			users.getProfile(),
			owner
		);
	}
}
