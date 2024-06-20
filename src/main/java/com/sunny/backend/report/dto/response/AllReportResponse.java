package com.sunny.backend.report.dto.response;

import java.time.LocalDateTime;

import com.sunny.backend.report.domain.CommunityReport;
import com.sunny.backend.report.domain.ReportStatus;

public record AllReportResponse(
	Long reportId,
	Long userId,
	Long reportUserId,
	String reportReason,
	LocalDateTime reportDate, //TODO 시간도 필요한지?
	ReportStatus reportStatus,
	Long communityId // TODO community or comment 일텐데 이거 id 구분 잘해야될 듯
) {
	public static AllReportResponse fromCommunityReport(CommunityReport communityReport) {
		return new AllReportResponse(
			communityReport.getId(),
			communityReport.getUsers().getId(), //신고한 사람
			communityReport.getCommunity().getUsers().getId(), //신고 당한사람
			communityReport.getReason(),
			communityReport.getCreatedDate(),
			communityReport.getStatus(),
			communityReport.getCommunity().getId()
		);
	}
}