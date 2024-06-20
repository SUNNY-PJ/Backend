package com.sunny.backend.report.dto.request;

import com.sunny.backend.report.domain.ReportType;

public record ReportRequest(
	Long id,
	ReportType reportType
) {
}
