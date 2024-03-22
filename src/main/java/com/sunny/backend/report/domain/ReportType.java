package com.sunny.backend.report.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
	COMMUNITY("커뮤니티"), COMMENT("댓글");

	private final String status;
}
