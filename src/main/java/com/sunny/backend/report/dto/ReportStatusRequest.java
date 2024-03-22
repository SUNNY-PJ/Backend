package com.sunny.backend.report.dto;

import com.sunny.backend.report.domain.ReportType;

public record ReportStatusRequest(
	Long id,
	ReportType status
) {
}
