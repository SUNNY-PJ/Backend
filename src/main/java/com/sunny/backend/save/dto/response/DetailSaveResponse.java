package com.sunny.backend.save.dto.response;

import com.sunny.backend.save.domain.Save;

public record DetailSaveResponse(
	Long id,
	long date,
	double savePercentage,
	Long cost
) {
	public static DetailSaveResponse of(Save save, Long userMoney) {
		return new DetailSaveResponse(
			save.getId(),
			save.calculateRemainingDays(),
			save.calculateSavePercentage(userMoney),
			save.getCost()
		);
	}
}
