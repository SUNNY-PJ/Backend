package com.sunny.backend.friends.dto.response;

import com.sunny.backend.friends.domain.Status;

public record FriendResponse(
	Long friendsId,
	Long competitionId,
	Long userFriendId,
	String name,
	String profile,
	Status friendStatus,
	Status competitionStatus
) {
	// public static FriendResponse from(Friend friend) {
	// 	return new FriendResponse(
	// 		friend.getId(),
	// 		friend.getUserFriend().getId(),
	// 		friend.getUserFriend().getName(),
	// 		friend.getUserFriend().getProfile(),
	// 		friend.getStatus()
	// 	);
	// }
}