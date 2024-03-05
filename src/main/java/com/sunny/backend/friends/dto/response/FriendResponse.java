package com.sunny.backend.friends.dto.response;

import com.sunny.backend.friends.domain.Status;

public record FriendResponse(
	Long friendsId,
	Long competitionId,
	Long userFriendId,
	String nickname,
	String profile,
	Status friendStatus,
	Status competitionStatus
) {
}
