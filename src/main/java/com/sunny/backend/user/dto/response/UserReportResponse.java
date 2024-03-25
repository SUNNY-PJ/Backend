package com.sunny.backend.user.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.report.domain.CommentReport;
import com.sunny.backend.report.domain.CommunityReport;

import lombok.Builder;

@Builder
public record UserReportResponse(
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDateTime time,
	String name,
	String body,
	String reason
) {

	public static UserReportResponse fromCommunityReport(CommunityReport communityReport) {
		return UserReportResponse.builder()
			.time(communityReport.getCreatedDate())
			.name(communityReport.getCommunity().getUsers().getNickname())
			.body(communityReport.getCommunity().getContents())
			.reason(communityReport.getReason())
			.build();
	}

	public static UserReportResponse fromCommentReport(CommentReport commentReport) {
		return UserReportResponse.builder()
			.time(commentReport.getCreatedDate())
			.name(commentReport.getComment().getUsers().getNickname())
			.body(commentReport.getComment().getContent())
			.reason(commentReport.getReason())
			.build();
	}
}
