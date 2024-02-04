package com.sunny.backend.report.repository;

import static com.sunny.backend.report.exception.ReportErrorCode.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.report.domain.CommentReport;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
	default CommentReport getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(REPORT_COMMENT_NOT_FOUND));
	}
}
