package com.sunny.backend.user.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.community.domain.Community;

import lombok.Builder;

@Builder
public record UserCommunityResponse(
	Long communityId,
	String title,
	String writer,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime createdDate,
	int viewCnt,
	int commentCnt
) {
	public static UserCommunityResponse from(Community community) {
		return UserCommunityResponse.builder()
			.communityId(community.getId())
			.title(community.getTitle())
			.writer(community.getUsers().getName())
			.createdDate(community.getCreatedAt())
			.viewCnt(community.getViewCnt())
			.commentCnt(community.getCommentSize())
			.build();
	}
}
