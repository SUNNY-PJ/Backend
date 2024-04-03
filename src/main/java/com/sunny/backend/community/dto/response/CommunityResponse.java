package com.sunny.backend.community.dto.response;

import java.util.List;
import java.util.Objects;

import com.sunny.backend.common.photo.Photo;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.util.DatetimeUtil;

public record CommunityResponse(
	Long id,
	Long userId,
	String writer,
	String title,
	String contents,
	int viewCount,
	List<String> photoList,
	int commentCnt,
	BoardType type,
	String profileImg,
	String createdAt,
	boolean isModified,
	boolean isScraped,
	boolean isAuthor
) {

	public static CommunityResponse from(Community community) {
		return new CommunityResponse(
			community.getId(),
			community.getUsers().getId(),
			community.getUsers().getNickname(),
			community.getTitle(),
			community.getContents(),
			community.getViewCnt(),
			community.getPhotos()
				.stream()
				.map(Photo::getFileUrl)
				.filter(Objects::nonNull)    // TODO 없어도 되지 않을까 싶은..?
				.toList(),
			community.getCommentSize(),
			community.getBoardType(),
			community.getUsers().getProfile(),
			DatetimeUtil.timesAgo(community.getCreatedAt()),
			community.hasNotBeenModified(),
			community.getUsers().isScrapByCommunity(community.getId()),
			community.isAuthor(community.getUsers().getId())
		);
	}

}