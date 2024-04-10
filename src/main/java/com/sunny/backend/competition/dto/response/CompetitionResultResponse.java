package com.sunny.backend.competition.dto.response;

import java.time.LocalDate;

import com.sunny.backend.competition.domain.Competition;

public record CompetitionResultResponse(
	LocalDate endDate,
	Long price,
	String compensation,
	String userFriendNickname,
	String message
) {
	public static CompetitionResultResponse of(Competition competition, String friendName, String message) {
		return new CompetitionResultResponse(
			competition.getEndDate(),
			competition.getPrice(),
			competition.getCompensation(),
			friendName,
			message
		);
	}
}
