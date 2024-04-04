package com.sunny.backend.competition.domain;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetitionOutput {

	public static final Long COMPETITION_NONE_VALUE = -2L;
	public static final Long COMPETITION_DRAW_VALUE = -1L;
	
	Long output;

	private CompetitionOutput(Long output) {
		this.output = output;
	}

	public static CompetitionOutput from(Long output) {
		return new CompetitionOutput(output);
	}

	public CompetitionOutputType isWinner(Long userId) {
		if (output.equals(COMPETITION_NONE_VALUE)) {
			return CompetitionOutputType.NONE;
		} else if (output.equals(COMPETITION_DRAW_VALUE)) {
			return CompetitionOutputType.DRAW;
		} else if (userId.equals(output)) {
			return CompetitionOutputType.WIN;
		}

		return CompetitionOutputType.LOSE;
	}

	public void updateOutput(double userPercent, double userFriendPercent, Long userId, Long userFriendId) {
		if (userPercent > userFriendPercent) {
			output = userId;
		} else if (userPercent < userFriendPercent) {
			output = userFriendId;
		} else {
			output = COMPETITION_DRAW_VALUE;
		}
	}
}
