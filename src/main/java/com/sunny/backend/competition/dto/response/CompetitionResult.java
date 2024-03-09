package com.sunny.backend.competition.dto.response;

import java.time.LocalDate;

import com.sunny.backend.competition.domain.CompetitionStatus;

import lombok.Builder;

@Builder
public record CompetitionResult(
	String userFriendNickname,
	CompetitionStatus output,
	LocalDate endDate,
	Long price,
	String compensation
) {
	public static CompetitionResult from(CompetitionResultDto dto) {
		return CompetitionResult.builder()
			.userFriendNickname(dto.userFriendNickname())
			.output(isWinner(dto.userId(), dto.output()))
			.endDate(dto.endDate())
			.price(dto.price())
			.compensation(dto.compensation())
			.build();
	}

	public static CompetitionStatus isWinner(Long userId, Long output) {
		if (userId.equals(output)) {
			return CompetitionStatus.WIN;
		} else if (output.equals(-1L)) {
			return CompetitionStatus.DRAW;
		} else {
			return CompetitionStatus.LOSE;
		}
	}
}
