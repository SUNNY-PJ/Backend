package com.sunny.backend.user.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.community.domain.Community;
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
	public static UserReportResponse toCommunity(LocalDateTime createdDate, Community community, String reason) {
		return UserReportResponse.builder()
			.time(createdDate)
			.name(community.getUsers().getNickname())
			.body(community.getContents())
			.reason(reason)
			.build();
	}

	public static UserReportResponse toComment(LocalDateTime createdDate, Comment comment, String reason) {
		return UserReportResponse.builder()
			.time(createdDate)
			.name(comment.getUsers().getNickname())
			.body(comment.getContent())
			.reason(reason)
			.build();
	}

	public static UserReportResponse toCommunityReport(CommunityReport communityReport) {
		return UserReportResponse.builder()
			.time(communityReport.getCreatedDate())
			.name(communityReport.getCommunity().getUsers().getNickname())
			.body(communityReport.getCommunity().getContents())
			.reason(communityReport.getReason())
			.build();
	}

	public static UserReportResponse toCommentReport(CommentReport commentReport) {
		return UserReportResponse.builder()
			.time(commentReport.getCreatedDate())
			.name(commentReport.getComment().getUsers().getNickname())
			.body(commentReport.getComment().getContent())
			.reason(commentReport.getReason())
			.build();
	}
}
