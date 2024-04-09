package com.sunny.backend.report.service;

import java.util.List;

import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.user.dto.response.ReportResponse;
import com.sunny.backend.user.dto.response.UserReportResponse;

public interface ReportStrategy {

	UserReportResponse report(Long userId, ReportCreateRequest reportCreateRequest);

	void approveUserReport(Long id);

	void refuseUserReport(Long id);

	ReportType getReportType();

	List<ReportResponse> getUserReports(ReportType reportType);

}
