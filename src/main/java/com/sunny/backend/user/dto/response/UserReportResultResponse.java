package com.sunny.backend.user.dto.response;

import com.sunny.backend.report.domain.CommentReport;
import com.sunny.backend.report.domain.CommunityReport;

public record UserReportResultResponse(
	UserReportResponse userReportResponse,
	String result
) {
	public static UserReportResultResponse ofCommunity(CommunityReport communityReport, boolean success) {
		String answer;
		if (success) {
			answer = "작성자에게 경고를 보냈습니다. 깨끗한 커뮤니티 이용을 위해 힘써주셔서 감사합니다.";
		} else {
			answer = "운영 원칙에 어긋나지 않는다고 판단되어 경고를 보내지 않았습니다. 깨끗한 커뮤니티 이용을 위해 힘써주셔서 감사합니다.!";
		}
		return new UserReportResultResponse(UserReportResponse.toCommunityReport(communityReport), answer);
	}

	public static UserReportResultResponse ofCommentReport(CommentReport commentReport, boolean success) {
		String answer;
		if (success) {
			answer = "작성자에게 경고를 보냈습니다. 깨끗한 커뮤니티 이용을 위해 힘써주셔서 감사합니다.";
		} else {
			answer = "운영 원칙에 어긋나지 않는다고 판단되어 경고를 보내지 않았습니다. 깨끗한 커뮤니티 이용을 위해 힘써주셔서 감사합니다.!";
		}
		return new UserReportResultResponse(UserReportResponse.toCommentReport(commentReport), answer);
	}
}
