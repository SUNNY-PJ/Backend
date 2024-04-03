package com.sunny.backend.scrap.dto.response;

import java.time.LocalDateTime;

import com.sunny.backend.community.domain.Community;

import lombok.Builder;

@Builder
public record ScrapResponse(
	String title,
	String writer,
	LocalDateTime createDate,
	int view,
	int comment

) {
	public static ScrapResponse from(Community community) {
		return ScrapResponse.builder()
			.title(community.getTitle())
			.writer(community.getUsers().getNickname())
			.createDate(community.getCreatedAt())
			.view(community.getViewCnt())
			.comment(community.getCommentSize())
			.build();
	}
}
