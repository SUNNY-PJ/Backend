package com.sunny.backend.report.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.report.dto.response.AllReportResponse;
import com.sunny.backend.report.service.ReportService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "10. Back-Offcie-Report", description = "Back-Offcie-Report API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/report/all")
public class ReportController {
	private final ReportService reportService;

	@GetMapping("/community")
	public List<AllReportResponse> ReportAllByCommunity() {
		return reportService.ReportResult();
	}

}
