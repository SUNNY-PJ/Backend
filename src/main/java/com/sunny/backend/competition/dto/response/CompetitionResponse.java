package com.sunny.backend.competition.dto.response;

import java.time.LocalDate;

import com.sunny.backend.friends.domain.Friend;

import lombok.Builder;

@Builder
public record CompetitionResponse(
	Long friendId,
	String message,
	String compensation,
	LocalDate startDate,
	LocalDate endDate,
	Integer period,
	Long price
) {
	public static CompetitionResponse from(Friend friend) {
		return CompetitionResponse.builder()
			.friendId(friend.getId())
			.message(friend.getCompetition().getMessage())
			.compensation(friend.getCompetition().getCompensation())
			.startDate(friend.getCompetition().getStartDate())
			.endDate(friend.getCompetition().getEndDate())
			.price(friend.getCompetition().getPrice())
			.build();
	}
}

