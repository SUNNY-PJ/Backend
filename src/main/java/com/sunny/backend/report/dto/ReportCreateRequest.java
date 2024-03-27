package com.sunny.backend.report.dto;

import com.sunny.backend.report.domain.ReportType;

public record ReportCreateRequest(
	Long id,
	ReportType reportType,
	String reason
) {
}
