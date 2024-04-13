package com.sunny.backend.friends.dto.response;

import java.time.LocalDate;

import com.sunny.backend.friends.domain.FriendCompetition;

public record FriendCompetitionResponses(
	Long friendId,
	Long competitionId,
	String message,
	String compensation,
	LocalDate startDate,
	LocalDate endDate,
	Long price
) {
	public static FriendCompetitionResponses from(FriendCompetition friendCompetition) {
		return new FriendCompetitionResponses(
			friendCompetition.getFriend().getId(),
			friendCompetition.getCompetition().getId(),
			friendCompetition.getCompetition().getMessage(),
			friendCompetition.getCompetition().getCompensation(),
			friendCompetition.getCompetition().getStartDate(),
			friendCompetition.getCompetition().getEndDate(),
			friendCompetition.getCompetition().getPrice()
		);
	}
}
