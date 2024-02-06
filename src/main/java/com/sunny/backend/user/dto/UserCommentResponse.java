package com.sunny.backend.user.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.comment.domain.Comment;

import lombok.Builder;

@Builder
public record UserCommentResponse (
	Long communityId,
	Long commentId,
	String content,
	String writer,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime createdDate
){
	public static UserCommentResponse from(Comment comment) {
		return UserCommentResponse.builder()
			.communityId(comment.getCommunity().getId())
			.commentId(comment.getId())
			.content(comment.getContent())
			.writer(comment.getUsers().getNickname())
			.createdDate(comment.getCreatedDate())
			.build();
	}
}
