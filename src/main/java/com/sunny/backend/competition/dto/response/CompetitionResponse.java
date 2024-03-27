package com.sunny.backend.competition.dto.response;

import com.sunny.backend.friends.domain.Friend;

import lombok.Builder;

@Builder
public record CompetitionResponse(
	Long friendId,
	String message,
	String compensation,
	Integer period,
	Long price
) {
	public static CompetitionResponse from(Friend friend) {
		return CompetitionResponse.builder()
			.friendId(friend.getId())
			.message(friend.getCompetition().getMessage())
			.compensation(friend.getCompetition().getCompensation())
			.period(friend.getCompetition().getPeriod())
			.price(friend.getCompetition().getPrice())
			.build();
	}
}

