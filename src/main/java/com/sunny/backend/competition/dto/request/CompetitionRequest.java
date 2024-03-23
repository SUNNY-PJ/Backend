package com.sunny.backend.competition.dto.request;

public record CompetitionRequest(
	Long friendsId,
	String message,
	Integer day,
	Long price,
	String compensation
) {
}
