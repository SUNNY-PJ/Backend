package com.sunny.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.report.dto.ReportRequest;
import com.sunny.backend.report.service.ReportServiceImpl;
import com.sunny.backend.user.dto.response.UserReportResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "0. User", description = "User API")
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserReportController {

	private final ReportServiceImpl reportService;

	@ApiOperation(tags = "0. User", value = "유저 신고")
	@PostMapping("/report")
	public ResponseEntity<UserReportResponse> reportCommunity(
		@AuthUser CustomUserPrincipal customUserPrincipal, @RequestBody ReportCreateRequest reportCreateRequest) {
		UserReportResponse userReportResponse = reportService.userReport(customUserPrincipal, reportCreateRequest);
		return ResponseEntity.ok().body(userReportResponse);
	}

	@ApiOperation(tags = "0. User", value = "유저 신고 승인")
	@PatchMapping("/report")
	public ResponseEntity<Void> approveUserReport(@RequestBody ReportRequest reportRequest) {
		reportService.approveUserReport(reportRequest);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(tags = "0. User", value = "유저 신고 거절")
	@DeleteMapping("/report")
	public ResponseEntity<Void> refuseUserReport(@RequestBody ReportRequest reportRequest) {
		reportService.refuseUserReport(reportRequest);
		return ResponseEntity.ok().build();
	}
}
