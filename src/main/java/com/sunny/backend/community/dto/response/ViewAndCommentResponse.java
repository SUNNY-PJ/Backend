package com.sunny.backend.community.dto.response;

import com.sunny.backend.community.domain.Community;

public record ViewAndCommentResponse(
	int viewCount,
	int commentCount

) {
	public static ViewAndCommentResponse from(Community community) {
		return new ViewAndCommentResponse(
			community.getViewCnt(),
			community.getCommentSize()
		);
	}
}