package com.sunny.backend.user.dto.response;

import com.sunny.backend.report.domain.CommentReport;
import com.sunny.backend.report.domain.CommunityReport;
import com.sunny.backend.report.domain.ReportStatus;

public record ReportResponse(
	Long id,
	String reason,
	ReportStatus reportStatus,
	Long reportSubjectId,
	Long userId
) {
	public static ReportResponse fromCommunityReport(CommunityReport communityReport) {
		return new ReportResponse(
			communityReport.getId(),
			communityReport.getReason(),
			communityReport.getStatus(),
			communityReport.getCommunity().getId(),
			communityReport.getUsers().getId()
		);
	}

	public static ReportResponse fromCommentReport(CommentReport commentReport) {
		return new ReportResponse(
			commentReport.getId(),
			commentReport.getReason(),
			commentReport.getStatus(),
			commentReport.getComment().getId(),
			commentReport.getUsers().getId()
		);
	}
}
