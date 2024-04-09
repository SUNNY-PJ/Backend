package com.sunny.backend.comment.dto.response;

import com.sunny.backend.user.domain.Users;
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
	private Long userId;
	private String content;
	private String writer;
	private String profileImg;
	private boolean isAuthor;
	private boolean commentAuthor;
	private boolean isDeleted;
	private boolean isPrivated;
	private boolean isRevokeUser;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime createdDate;
	private List<CommentResponse> children = new ArrayList<>();

	public CommentResponse(Long id, Long userId,String writer, String content, LocalDateTime createdDate,
			String profileImg,boolean isAuthor,boolean commentAuthor,boolean isDeleted,boolean isPrivated,boolean isRevokeUser) {
		this.id = id;
		this.userId=userId;
		this.writer = writer;
		this.content = content;
		this.createdDate = createdDate;
		this.profileImg=profileImg;
		this.isAuthor = isAuthor;
		this.commentAuthor=commentAuthor;
		this.isDeleted=isDeleted;
		this.isPrivated=isPrivated;
		this.isRevokeUser=isRevokeUser;
	}

	//삭제된 댓글로 댓글 내용 수정하기 위한 객체 생성
	public static CommentResponse convertCommentToDto(Users users,Comment comment) {
		boolean commentAuthor = users.getId().equals(comment.getUsers().getId());
		if (comment.getIsDeleted()) {
			return new CommentResponse(
					comment.getId(),
					null,
					null,
					"삭제된 댓글입니다.",
					null,
					null,
					comment.getAuthor(),
					commentAuthor,
					true,
					false,
					false
			);
		} else {
			String writer = comment.getUsers() != null ? comment.getUsers().getNickname() : null;
			String content = comment.getContent();
			LocalDateTime createdDate = comment.getCreatedDate();
			boolean isAuthor =comment.getAuthor();
			return new CommentResponse(comment.getId(),comment.getUsers().getId(), writer, content, createdDate,
					users.getProfile(), isAuthor,commentAuthor,false,comment.getIsPrivated(),false);
		}
	}

	public static CommentResponse leaveCommentToDto(Users users, Comment comment) {

			return new CommentResponse(
					comment.getId(),
					null,
					"(알 수 없음)",
					"탈퇴한 회원의 댓글입니다.",
					null,
					null,
					comment.getAuthor(),
					false,
					comment.getIsDeleted(),
					comment.getIsPrivated(),
					true
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
					.writer(comment.getUsers().getNickname())
					.createdDate(comment.getCreatedDate())
					.updateDate(comment.getUpdatedDate())
					.build();
		}
	}
}
