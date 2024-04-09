package com.sunny.backend.competition.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

public record CompetitionRequest(
	Long friendsId,
	String message,
	@JsonFormat(pattern = "yyyy.MM.dd")
	@NotNull(message = "시작 날짜는 필수 입력값입니다.")
	LocalDate startDate,
	@JsonFormat(pattern = "yyyy.MM.dd")
	@NotNull(message = "종료 날짜는 필수 입력값입니다.")
	LocalDate endDate,
	Long price,
	String compensation
) {
}
