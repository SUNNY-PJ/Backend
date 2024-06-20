package com.sunny.backend.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.request.ReportCreateRequest;
import com.sunny.backend.report.service.ReportService;
import com.sunny.backend.user.dto.response.ReportResponse;
import com.sunny.backend.user.dto.response.UserReportResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "0. User", description = "User API")
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserReportController {

	private final ReportService reportService;

	@ApiOperation(tags = "0. User", value = "유저 신고 목록 확인")
	@GetMapping("/report")
	public ResponseEntity<List<ReportResponse>> getUserReports(
		@RequestParam ReportType reportType
	) {
		List<ReportResponse> response = reportService.getUserReports(reportType);
		return ResponseEntity.ok().body(response);
	}

	@ApiOperation(tags = "0. User", value = "유저 신고")
	@PostMapping("/report")
	public ResponseEntity<UserReportResponse> createUserReport(
		@AuthUser CustomUserPrincipal customUserPrincipal, @RequestBody ReportCreateRequest reportCreateRequest) {
		UserReportResponse userReportResponse = reportService.createUserReport(customUserPrincipal,
			reportCreateRequest);
		return ResponseEntity.ok().body(userReportResponse);
	}

	@ApiOperation(tags = "0. User", value = "유저 신고 승인")
	@PatchMapping("/report/{id}")
	public ResponseEntity<Void> approveUserReport(
		@PathVariable(name = "id") Long id,
		@RequestParam ReportType reportType
	) {
		reportService.approveUserReport(id, reportType);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(tags = "0. User", value = "유저 신고 거절")
	@DeleteMapping("/report/{id}")
	public ResponseEntity<Void> refuseUserReport(
		@PathVariable(name = "id") Long id,
		@RequestParam ReportType reportType
	) {
		reportService.refuseUserReport(id, reportType);
		return ResponseEntity.ok().build();
	}
}
