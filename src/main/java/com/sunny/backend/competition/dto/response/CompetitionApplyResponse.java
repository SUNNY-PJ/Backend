package com.sunny.backend.competition.dto.response;

import com.sunny.backend.competition.domain.Competition;

import lombok.Builder;

@Builder
public record CompetitionApplyResponse(
	Long friendId,
	String message,
	String applyMessage,
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
			.applyMessage(name + "님에게서 대결 신청이 왔어요!")
			.compensation(competition.getCompensation())
			.period(competition.getPeriod())
			.price(competition.getPrice())
			.build();
	}
}

