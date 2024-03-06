package com.sunny.backend.competition.dto.response;

import com.sunny.backend.friends.domain.Friend;

import lombok.Builder;

@Builder
public record CompetitionResponse(
	Long userFriendId,
	Long competitionId,
	String status
) {
	public static CompetitionResponse from(Friend friend) {
		return CompetitionResponse.builder()
			.userFriendId(friend.getUserFriend().getId())
			.competitionId(friend.getCompetition().getId())
			.status(friend.getCompetition().getStatus().getStatus())
			.build();
	}
}
