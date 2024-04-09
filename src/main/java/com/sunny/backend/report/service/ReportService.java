package com.sunny.backend.report.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.user.dto.response.ReportResponse;
import com.sunny.backend.user.dto.response.UserReportResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportFactory reportFactory;

	public List<ReportResponse> getUserReports(ReportType reportType) {
		ReportStrategy reportStrategy = reportFactory.findReportStrategy(reportType);
		return reportStrategy.getUserReports(reportType);
	}

	public UserReportResponse createUserReport(
		CustomUserPrincipal customUserPrincipal,
		ReportCreateRequest reportCreateRequest
	) {
		ReportStrategy reportStrategy = reportFactory.findReportStrategy(reportCreateRequest.reportType());
		return reportStrategy.report(customUserPrincipal.getId(), reportCreateRequest);
	}

	@Transactional
	public void approveUserReport(Long id, ReportType reportType) {
		ReportStrategy reportStrategy = reportFactory.findReportStrategy(reportType);
		reportStrategy.approveUserReport(id);
	}

	@Transactional
	public void refuseUserReport(Long id, ReportType reportType) {
		ReportStrategy reportStrategy = reportFactory.findReportStrategy(reportType);
		reportStrategy.refuseUserReport(id);
	}

}
