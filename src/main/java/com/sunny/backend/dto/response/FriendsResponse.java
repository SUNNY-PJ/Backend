package com.sunny.backend.dto.response;

import com.sunny.backend.friends.domain.Friends;

public record FriendsResponse (
	Long friendsSn,
	Long friendsId,
	String name,
	String profile
) {
	public static FriendsResponse from(Friends friends) {
		return new FriendsResponse(
			friends.getFriendsSn(),
			friends.getFriend().getId(),
			friends.getFriend().getName(),
			friends.getFriend().getProfile()
		);
	}
}
