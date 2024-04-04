package com.sunny.backend.report.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunny.backend.report.domain.ReportType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportFactory {
	private Map<ReportType, ReportStrategy> reportStrategyMap;

	@Autowired
	public ReportFactory(Set<ReportStrategy> reportStrategySet) {
		createReportStrategyMap(reportStrategySet);
	}

	private void createReportStrategyMap(Set<ReportStrategy> reportStrategySet) {
		reportStrategyMap = new HashMap<>();
		reportStrategySet.forEach(
			reportStrategy -> reportStrategyMap.put(
				reportStrategy.getReportType(), reportStrategy
			)
		);
	}

	public ReportStrategy findReportStrategy(ReportType reportType) {
		return reportStrategyMap.get(reportType);
	}

}
