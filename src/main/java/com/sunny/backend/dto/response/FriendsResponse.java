package com.sunny.backend.dto.response;

import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.domain.Friends;

public record FriendsResponse (
	Long friendsSn,
	Long friendsId,
	String name,
	String profile,
	FriendStatus status
) {
	public static FriendsResponse from(Friends friends) {
		return new FriendsResponse(
			friends.getFriendsSn(),
			friends.getFriend().getId(),
			friends.getUsers().getName(),
			friends.getUsers().getProfile(),
			friends.getStatus()
		);
	}
}
