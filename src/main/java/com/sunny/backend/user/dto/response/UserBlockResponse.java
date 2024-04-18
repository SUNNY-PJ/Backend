package com.sunny.backend.user.dto.response;

import com.sunny.backend.user.domain.Users;

public record UserBlockResponse (
	Long id,
	String nickname,
	String profile
) {
	public static UserBlockResponse from(Users users) {
		return new UserBlockResponse(
			users.getId(),
			users.getNickname(),
			users.getProfile()
		);
	}
}
