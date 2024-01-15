package com.sunny.backend.dto.response;

import com.sunny.backend.friends.domain.FriendStatus;

public record FriendCheckResponse(
	boolean isFriend,
	FriendStatus status
) {
}
