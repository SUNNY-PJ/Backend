package com.sunny.backend.user.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.community.domain.Community;

import lombok.Builder;

@Builder
public record UserScrapResponse(
	String title,
	String writer,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime createDate,
	int viewCnt,
	int commentCnt
) {
	public static UserScrapResponse from(Community community) {
		return UserScrapResponse.builder()
			.title(community.getTitle())
			.writer(community.getUsers().getName())
			.createDate(community.getCreatedAt())
			.viewCnt(community.getViewCnt())
			.commentCnt(community.getCommentSize())
			.build();
	}
}
