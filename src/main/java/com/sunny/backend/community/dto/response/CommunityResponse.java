package com.sunny.backend.community.dto.response;

import com.sunny.backend.util.DatetimeUtil;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.common.photo.Photo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    boolean isScraped
) {

    public static CommunityResponse of(Community community, boolean isScraped) {
        boolean isModified = community.hasNotBeenModified(community.getCreatedAt(), community.getModifiedAt());

        return new CommunityResponse(
            community.getId(),
            community.getUsers().getId(),
            community.getUsers().getName(),
            community.getTitle(),
            community.getContents(),
            community.getViewCnt(),
            community.getPhotoList()
                .stream()
                .map(Photo::getFileUrl)
                .filter(Objects::nonNull)
                .toList(),
            community.getCommentList().size(),
            community.getBoardType(),
            community.getUsers().getProfile(),
            DatetimeUtil.timesAgo(community.getCreatedAt()),
            isModified,
            isScraped
        );
    }
    public record PageResponse(
        Long id,
        Long userId,
        String title,
        String writer,
        int viewCount,
        int commentCount,
        String createdAt,
        boolean isModified
    ) {
        public static PageResponse from(Community community) {
            boolean isModified = community.hasNotBeenModified(community.getCreatedAt(), community.getModifiedAt());
            return new PageResponse(
                community.getId(),
                community.getUsers().getId(),
                community.getTitle(),
                community.getUsers().getName(),
                community.getViewCnt(),
                community.getCommentList().size(),
                DatetimeUtil.timesAgo(community.getCreatedAt()),
                isModified
            );
        }
    }
    public record ViewAndCommentResponse(
        int viewCount,
        int commentCount

    ) {
        public static ViewAndCommentResponse from(Community community) {
            return new ViewAndCommentResponse(
                community.getViewCnt(),
                community.getCommentList().size()
            );
        }
    }
}