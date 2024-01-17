package com.sunny.backend.friends.dto.response;

import com.sunny.backend.friends.domain.Status;

public record FriendCheckResponse(
	boolean isFriend,
	Status status
) {
}
