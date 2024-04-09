package com.sunny.backend.save.dto.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.sunny.backend.save.domain.Save;

public record SaveResponse(
	Long id,
	Long cost,
	boolean expire,
	String startDate,
	String endDate
) {
	public static SaveResponse from(Save save) {
		return new SaveResponse(
			save.getId(),
			save.getCost(),
			save.checkExpired(),
			formatStartDateWithDayOfWeek(save.getStartDate()),
			formatStartDateWithDayOfWeek(save.getEndDate())
		);
	}

	private static String formatStartDateWithDayOfWeek(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE", Locale.KOREA));
	}
}