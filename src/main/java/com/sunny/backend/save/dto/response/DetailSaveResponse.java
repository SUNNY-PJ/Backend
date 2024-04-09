package com.sunny.backend.save.dto.response;

public record DetailSaveResponse(
	long date,
	double savePercentage,
	Long cost
) {
	public static DetailSaveResponse of(long date, double savePercentage, long cost) {
		return new DetailSaveResponse(
			date,
			savePercentage,
			cost
		);
	}
}
