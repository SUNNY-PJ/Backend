package com.sunny.backend.competition.dto.request;

import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.friends.domain.Status;

public record CompetitionRequest(
	Long friendsId,
	String message,
	Long price,
	String compensation,
	Integer day
) {

	public Competition toEntity() {
		return Competition.builder()
			.message(message)
			.price(price)
			.day(day)
			.status(Status.WAIT)
			.build();
	}
}
