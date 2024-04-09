package com.sunny.backend.competition.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

public class CompetitionRequestDto {

	@Getter
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class CompetitionApply {
		private Long friendsId;
		private String message;
		private Long price;
		private String compensation;
		private LocalDate startDate;
		private LocalDate endDate;
	}

	@Getter
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class CompetitionAccept {
		private Long competitionId;
		private Character approve;
	}

}
