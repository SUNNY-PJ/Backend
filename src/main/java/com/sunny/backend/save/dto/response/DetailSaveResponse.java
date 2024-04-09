package com.sunny.backend.save.dto.response;

public record DetailSaveResponse(
	Long id,
	long date,
	double savePercentage,
	Long cost
) {
	public static DetailSaveResponse of(Long id, long date, double savePercentage, long cost) {
		return new DetailSaveResponse(
			id,
			date,
			savePercentage,
			cost
		);
	}
}
