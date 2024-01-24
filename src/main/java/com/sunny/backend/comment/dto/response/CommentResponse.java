package com.sunny.backend.comment.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponse {
	private Long id;
	private String content;
	private String writer;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime createdDate;
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
//	private LocalDateTime updateDate;
	private List<CommentResponse> children = new ArrayList<>();

	public CommentResponse(Long id, String writer, String content, LocalDateTime createdDate) {
		this.id = id;
		this.writer = writer;
		this.content = content;
		this.createdDate = createdDate;
	}

	//삭제된 댓글로 댓글 내용 수정하기 위한 객체 생성
	public static CommentResponse convertCommentToDto(Comment comment) {
		if (comment.getIsDeleted()) {
			comment.setContent("삭제된 댓글입니다.");
			comment.setUsers(null);
			comment.setCreatedDate(null);
			comment.setUpdatedDate(null);
		}

		return new CommentResponse(
				comment.getId(),
				comment.getContent(),
				comment.getUsers() != null ? comment.getUsers().getName() : null,
				comment.getCreatedDate()
		);
	}
	@Getter
	@Builder
	public static class MyComment {

		private Long communityId;
		private Long commentId;
		private String content;
		private String writer;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
		private LocalDateTime createdDate;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
		private LocalDateTime updateDate;

		public static MyComment from(Comment comment) {
			return MyComment.builder()
				.communityId(comment.getCommunity().getId())
				.commentId(comment.getId())
				.content(comment.getContent())
				.writer(comment.getUsers().getName())
				.createdDate(comment.getCreatedDate())
				.updateDate(comment.getUpdatedDate())
				.build();
		}
	}
}
