package com.sunny.backend.report.dto;

import com.sunny.backend.report.domain.ReportType;

public record ReportRequest(
	Long id,
	ReportType status,
	String reason
){
}
