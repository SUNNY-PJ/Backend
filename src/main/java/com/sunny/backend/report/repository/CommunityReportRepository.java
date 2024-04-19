package com.sunny.backend.report.repository;

import static com.sunny.backend.report.exception.ReportErrorCode.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.report.domain.CommunityReport;
import com.sunny.backend.user.domain.Users;

public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {
	void deleteAllByCommunityInOrUsers(List<Community> community, Users users);

	List<CommunityReport> findByCommunity_Id(Long communityId);

	default CommunityReport getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(REPORT_COMMUNITY_NOT_FOUND));
	}
}
