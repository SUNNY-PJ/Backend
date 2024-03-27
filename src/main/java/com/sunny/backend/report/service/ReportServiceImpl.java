package com.sunny.backend.report.service;

import static com.sunny.backend.report.exception.ReportErrorCode.*;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.report.dto.ReportRequest;
import com.sunny.backend.user.dto.response.UserReportResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl {
	private final List<ReportService> reportServices;

	public UserReportResponse userReport(CustomUserPrincipal customUserPrincipal,
		ReportCreateRequest reportCreateRequest) {
		return reportServices.stream()
			.filter(reportService -> reportService.isReportType(reportCreateRequest.reportType()))
			.findAny()
			.orElseThrow(() -> new CustomException(INVALID_REPORT_TYPE))
			.report(customUserPrincipal, reportCreateRequest);
	}

	@Transactional
	public void approveUserReport(ReportRequest reportRequest) {
		reportServices.stream()
			.filter(reportService -> reportService.isReportType(reportRequest.reportType()))
			.findAny()
			.orElseThrow(() -> new CustomException(INVALID_REPORT_TYPE))
			.approveUserReport(reportRequest);
	}

	@Transactional
	public void refuseUserReport(ReportRequest reportRequest) {
		reportServices.stream()
			.filter(reportService -> reportService.isReportType(reportRequest.reportType()))
			.findAny()
			.orElseThrow(() -> new CustomException(INVALID_REPORT_TYPE))
			.refuseUserReport(reportRequest);
	}
}
