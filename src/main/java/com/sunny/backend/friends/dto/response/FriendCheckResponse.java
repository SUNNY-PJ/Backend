package com.sunny.backend.friends.dto.response;

import com.sunny.backend.friends.domain.FriendStatus;

public record FriendCheckResponse(
	boolean isFriend,
	FriendStatus friendStatus
) {
}
