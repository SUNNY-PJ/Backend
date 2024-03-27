package com.sunny.backend.competition.dto.response;

import java.time.LocalDate;

import com.sunny.backend.competition.domain.CompetitionOutputType;

import lombok.Builder;

@Builder
public record CompetitionResult(
	String userFriendNickname,
	CompetitionOutputType output,
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

	public static CompetitionOutputType isWinner(Long userId, Long output) {
		if (userId.equals(output)) {
			return CompetitionOutputType.WIN;
		} else if (output.equals(-1L)) {
			return CompetitionOutputType.DRAW;
		} else {
			return CompetitionOutputType.LOSE;
		}
	}
}
