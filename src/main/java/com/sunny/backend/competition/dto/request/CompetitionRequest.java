package com.sunny.backend.competition.dto.request;

import java.time.LocalDate;

import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.friends.domain.Status;

public record CompetitionRequest(
	Long friendsId,
	String message,
	Long price,
	String compensation,
	LocalDate startDate,
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
