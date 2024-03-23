package com.sunny.backend.competition.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.competition.domain.Competition;

import lombok.Builder;

@Builder
public record CompetitionResponse(
	Long userFriendId,
	String compensation,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
	LocalDate startDate,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
	LocalDate endDate,
	Long price,
	String message
) {
	public static CompetitionResponse of(Long userFriendId, Competition competition) {
		return CompetitionResponse.builder()
			.userFriendId(userFriendId)
			.compensation(competition.getCompensation())
			.startDate(competition.getStartDate())
			.endDate(competition.getEndDate())
			.price(competition.getPrice())
			.message(competition.getMessage())
			.build();
	}
}
