package com.sunny.backend.util;

public class MathUtil {
	public static double calculatePercentage(Long usedMoney, Long divideMoney) {
		if (usedMoney == null) {
			return 100.0;
		}
		double percentage = 100.0 - ((usedMoney * 100.0) / divideMoney);
		return Math.round(percentage * 10) / 10.0; // 소수점 첫째 자리 반올림
	}
}
