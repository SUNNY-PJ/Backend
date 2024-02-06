package com.sunny.backend.user.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.community.domain.Community;

import lombok.Builder;

@Builder
public record UserReportResponse(
	@JsonFormat(pattern = "yyyy.MM.dd")
	LocalDateTime time,
	String name,
	String body,
	String reason
){
	public static UserReportResponse toCommunity(LocalDateTime createdDate, Community community, String reason) {
		return UserReportResponse.builder()
			.time(createdDate)
			.name(community.getUsers().getName())
			.body(community.getContents())
			.reason(reason)
			.build();
	}

	public static UserReportResponse toComment(LocalDateTime createdDate, Comment comment, String reason) {
		return UserReportResponse.builder()
			.time(createdDate)
			.name(comment.getUsers().getName())
			.body(comment.getContent())
			.reason(reason)
			.build();
	}
}
