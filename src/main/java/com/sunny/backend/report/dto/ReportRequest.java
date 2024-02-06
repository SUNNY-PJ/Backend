package com.sunny.backend.report.dto;

import com.sunny.backend.report.domain.ReportStatus;

public record ReportRequest(
	Long id,
	ReportStatus status,
	String reason
){
}
