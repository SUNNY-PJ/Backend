package com.sunny.backend.competition.dto.request;

import java.time.LocalDate;

import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.friends.domain.Status;
import com.sunny.backend.user.Users;

public record CompetitionRequest(
	Long friendsId,
	String message,
	Long price,
	String compensation,
	LocalDate startDate,
	LocalDate endDate
) {

	public Competition of(Users user, Users userFriend) {
		return Competition.builder()
			.message(message)
			.price(price)
			.compensation(compensation)
			.startDate(startDate)
			.endDate(endDate)
			.users(user)
			.userFriend(userFriend)
			.status(Status.WAIT)
			.build();
	}
}
