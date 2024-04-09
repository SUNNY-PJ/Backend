package com.sunny.backend.comment.dto.request;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentRequest {
	private Long parentId;
	@NotBlank(message = "댓글 내용은 필수 입력 값입니다.")
	private String content;
	private Boolean isPrivated;

	public CommentRequest(String content) {
		this.content = content;
	}
}
