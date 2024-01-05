package com.sunny.backend.dto.response;

import com.sunny.backend.friends.domain.Friend;

public record FriendsResponse (
	Long friendsSn,
	Long friendsId,
	String name,
	String profile
) {
	public static FriendsResponse from(Friend friend) {
		return new FriendsResponse(
			friend.getId(),
			friend.getUserFriend().getId(),
			friend.getUserFriend().getName(),
			friend.getUserFriend().getProfile()
		);
	}
}
