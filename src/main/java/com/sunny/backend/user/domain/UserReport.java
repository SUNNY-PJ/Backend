package com.sunny.backend.user.domain;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReport {
	public final int USER_LIMIT_REPORT = 5;

	int reportCount;

	private UserReport(int reportCount) {
		this.reportCount = reportCount;
	}

	public static UserReport from(int reportCount) {
		return new UserReport(reportCount);
	}

	public void increase() {
		this.reportCount++;
	}

	public boolean isReportLimitReached() {
		return reportCount >= USER_LIMIT_REPORT;
	}
}
