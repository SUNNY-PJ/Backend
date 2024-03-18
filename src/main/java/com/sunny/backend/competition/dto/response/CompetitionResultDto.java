package com.sunny.backend.competition.dto.response;

import java.time.LocalDate;

public record CompetitionResultDto(
	Long userId,
	Long userFriendId,
	String userFriendNickname,
	Long output,
	LocalDate endDate,
	Long price,
	String compensation
) {
}
