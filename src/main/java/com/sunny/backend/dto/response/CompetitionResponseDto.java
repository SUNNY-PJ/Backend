package com.sunny.backend.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CompetitionResponseDto {
	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class CompetitionStatus {
		private Long competitionId;
		private Integer price;
		private String compensation;
		private LocalDate endDate;
		private long dDay;
		private String username;
		private String friendName;
		private Integer userPercent;
		private Integer friendsPercent;
		private String result;
	}
}
