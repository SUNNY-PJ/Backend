package com.sunny.backend.user.dto;

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
			.writer(community.getUsers().getName())
			.createDate(community.getCreatedAt())
			.view(community.getView_cnt())
			.comment(community.getCommentList().size())
			.build();
	}
}
