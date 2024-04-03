package com.sunny.backend.community.dto.response;

import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.util.DatetimeUtil;

public record CommunityPageResponse(
	Long id,
	Long userId,
	String title,
	String writer,
	int viewCount,
	int commentCount,
	BoardType type,
	String createdAt,
	boolean isModified,
	boolean isAuthor
) {
	public static CommunityPageResponse of(Users users, Community community) {
		return new CommunityPageResponse(
			community.getId(),
			community.getUsers().getId(),
			community.getTitle(),
			community.getUsers().getNickname(),
			community.getViewCnt(),
			community.getCommentSize(),
			community.getBoardType(),
			DatetimeUtil.timesAgo(community.getCreatedAt()),
			community.hasNotBeenModified(),
			community.isAuthor(users.getId())
		);
	}
}