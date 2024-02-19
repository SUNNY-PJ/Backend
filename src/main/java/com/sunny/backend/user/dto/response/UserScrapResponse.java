package com.sunny.backend.user.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.community.domain.Community;

import lombok.Builder;

@Builder
public record UserScrapResponse(
	Long communityId,
	Long userId,
	String title,
	String writer,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime createDate,
	int viewCnt,
	int commentCnt
) {
	public static UserScrapResponse from(Community community) {
		return UserScrapResponse.builder()
			.communityId(community.getId())
			.userId(community.getUsers().getId())
			.title(community.getTitle())
			.writer(community.getUsers().getNickname())
			.createDate(community.getCreatedAt())
			.viewCnt(community.getViewCnt())
			.commentCnt(community.getCommentSize())
			.build();
	}
}
