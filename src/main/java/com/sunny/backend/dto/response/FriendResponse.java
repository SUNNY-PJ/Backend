package com.sunny.backend.dto.response;

import com.sunny.backend.friends.domain.Friend;

public record FriendResponse(
	Long friendsSn,
	Long friendsId,
	String name,
	String profile
) {
	public static FriendResponse from(Friend friend) {
		return new FriendResponse(
			friend.getId(),
			friend.getUserFriend().getId(),
			friend.getUserFriend().getName(),
			friend.getUserFriend().getProfile()
		);
	}
}
