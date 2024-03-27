package com.sunny.backend.friends.dto.response;

import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.friends.domain.FriendStatus;

public record FriendDto(
	Long userId,
	Long friendId,
	Long competitionId,
	Long userFriendId,
	String nickname,
	String profile,
	FriendStatus friendFriendStatus,
	CompetitionStatus competitionStatus,
	Long output
) {
}
