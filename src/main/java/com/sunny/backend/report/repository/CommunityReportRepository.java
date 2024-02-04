package com.sunny.backend.report.repository;

import static com.sunny.backend.report.exception.ReportErrorCode.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.report.domain.CommunityReport;

public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {
	default CommunityReport getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(REPORT_COMMUNITY_NOT_FOUND));
	}
}
