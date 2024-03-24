package com.sunny.backend.competition.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetitionOutput {

	public static final Long COMPETITION_DEFAULT_VALUE = -1L;
	Long output;

	private CompetitionOutput(Long output) { this.output = output; }

	public static CompetitionOutput from(Long output) {
		return new CompetitionOutput(output);
	}

	public CompetitionOutputType isWinner(Long userId) {
		if(Objects.equals(userId, COMPETITION_DEFAULT_VALUE)) {
			return CompetitionOutputType.DRAW;
		} else if(Objects.equals(userId, output)) {
			return CompetitionOutputType.WIN;
		}

		return CompetitionOutputType.LOSE;
	}
}
