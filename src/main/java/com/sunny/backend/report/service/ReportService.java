package com.sunny.backend.report.service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.report.dto.ReportRequest;
import com.sunny.backend.user.dto.response.UserReportResponse;

public interface ReportService {

	UserReportResponse report(CustomUserPrincipal customUserPrincipal, ReportCreateRequest reportCreateRequest);

	void approveUserReport(ReportRequest reportRequest);

	void refuseUserReport(ReportRequest reportRequest);

	boolean isReportType(ReportType reportType);

}
