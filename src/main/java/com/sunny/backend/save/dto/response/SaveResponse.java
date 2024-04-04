package com.sunny.backend.save.dto.response;

import java.time.LocalDate;

import com.sunny.backend.save.domain.Save;

public record SaveResponse(
	Long id,
	Long cost,
	boolean expire,
	boolean success,
	LocalDate startDate,
	LocalDate endDate
) {
	public static SaveResponse from(Save save, boolean success) {
		return new SaveResponse(
			save.getId(),
			save.getCost(),
			save.checkExpired(save.getEndDate()),
			success,
			save.getStartDate(), // 수정
			save.getEndDate()    // 수정
		);
	}
}