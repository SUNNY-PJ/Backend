package com.sunny.backend.competition.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.friends.domain.Status;

public record CompetitionRequest(
	Long friendsId,
	String message,
	Long price,
	String compensation,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
	LocalDate startDate,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
	LocalDate endDate
) {

	public Competition toEntity() {
		return Competition.builder()
			.message(message)
			.price(price)
			.compensation(compensation)
			.startDate(startDate)
			.endDate(endDate)
			.status(Status.WAIT)
			.build();
	}
}
