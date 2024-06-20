package com.sunny.backend.community.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.sunny.backend.community.domain.BoardType;

import lombok.Getter;

@Getter
public class CommunityRequest {
	@NotBlank(message = "제목은 필수 입력값입니다.")
	private String title;

	@NotBlank(message = "내용은 필수 입력값입니다.")
	private String contents;

	@NotNull(message = "올바른 카테고리 값을 입력해야합니다.")
	private BoardType type;
}
