package com.sunny.backend.competition.dto.response;

import com.sunny.backend.competition.domain.Competition;

import lombok.Builder;

@Builder
public record CompetitionApplyResponse(
	Long friendId,
	String message,
	String compensation,
	Integer period,
	Long price
) {
	public static CompetitionApplyResponse of(Long friendId, String name, Competition competition) {
		// long duration = Duration.between(competition.getStartDate().atStartOfDay(),
		// 		competition.getEndDate().atStartOfDay()).toDays();
		return CompetitionApplyResponse.builder()
			.friendId(friendId)
			.message(competition.getMessage())
			.compensation(competition.getCompensation())
			.period(competition.getPeriod())
			.price(competition.getPrice())
			.build();
	}
}

