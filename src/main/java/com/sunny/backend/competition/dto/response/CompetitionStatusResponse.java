package com.sunny.backend.competition.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompetitionStatusResponse {
	private Long competitionId;
	private Long price;
	private String compensation;
	private LocalDate endDate;
	private long day;
	private String username;
	private String friendName;
	private double userPercent;
	private double friendsPercent;
}
