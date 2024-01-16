package com.sunny.backend.dto.response;

import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.Status;

public record FriendResponse(
	Long friendsId,
	Long userFriendId,
	String name,
	String profile,
	Status status
) {
	public static FriendResponse from(Friend friend) {
		return new FriendResponse(
			friend.getId(),
			friend.getUserFriend().getId(),
			friend.getUserFriend().getName(),
			friend.getUserFriend().getProfile(),
			friend.getStatus()
		);
	}
}
