package com.sunny.backend.report.dto;

import com.sunny.backend.report.domain.ReportStatus;

public record ReportStatusRequest(
	Long id,
	ReportStatus status
) {
}
