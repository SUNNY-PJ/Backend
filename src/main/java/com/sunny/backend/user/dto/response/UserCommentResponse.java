package com.sunny.backend.user.dto.response;

import com.sunny.backend.user.domain.Users;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.comment.domain.Comment;

import lombok.Builder;

@Builder
public record UserCommentResponse(
	Long communityId,
	Long userId,
	Long commentId,
	String content,
	String writer,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime createdDate
) {

		public static UserCommentResponse from(Comment comment, Users currentUser) {
			String commentContent = "";
			if (comment.getIsPrivated() && !(currentUser.getId().equals(comment.getUsers().getId()) ||
					currentUser.getId().equals(comment.getCommunity().getUsers().getId()))) {
				if (comment.getIsDeleted()) {
					commentContent = "삭제된 댓글입니다.";
				} else {
					commentContent = "비밀 댓글입니다.";
				}
			} else {
				commentContent = comment.getContent();
			}

			return UserCommentResponse.builder()
					.communityId(comment.getCommunity().getId())
					.userId(comment.getUsers().getId())
					.commentId(comment.getId())
					.content(commentContent)
					.writer(comment.getUsers().getNickname())
					.createdDate(comment.getCreatedDate())
					.build();
		}
	}
