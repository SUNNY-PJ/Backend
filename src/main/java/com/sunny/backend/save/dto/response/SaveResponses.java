package com.sunny.backend.save.dto.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.sunny.backend.save.domain.Save;

public record SaveResponses(
	Long id,
	Long cost,
	boolean expire,
	boolean success,
	String startDate,
	String endDate
) {
	public static SaveResponses from(Save save, boolean success) {
		return new SaveResponses(
			save.getId(),
			save.getCost(),
			save.checkExpired(save.getEndDate()),
			success,
			formatStartDateWithDayOfWeek(save.getStartDate()),
			formatStartDateWithDayOfWeek(save.getEndDate())
		);
	}

	private static String formatStartDateWithDayOfWeek(LocalDate date) {
		String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE", Locale.KOREA));
		return formattedDate;
	}
}
