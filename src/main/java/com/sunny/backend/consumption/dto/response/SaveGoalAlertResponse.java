package com.sunny.backend.consumption.dto.response;

public record SaveGoalAlertResponse(
	Long goalSave,
	double percentage,
	String msg
) {
}
