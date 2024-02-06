package com.sunny.backend.community.dto.response;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.QUsers;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.util.DatetimeUtil;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.common.photo.Photo;

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
            community.getUsers().getNickname(),
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
        BoardType type,
        String createdAt,
        boolean isModified,
        boolean isAuthor
    ) {
        public static PageResponse from(Users users,Community community) {
            boolean isModified = community.hasNotBeenModified(community.getCreatedAt(), community.getModifiedAt());
            boolean isAuthor = community.getUsers().getId().equals(users.getId());
            return new PageResponse(
                community.getId(),
                community.getUsers().getId(),
                community.getTitle(),
                community.getUsers().getNickname(),
                community.getViewCnt(),
                community.getCommentList().size(),
                community.getBoardType(),
                DatetimeUtil.timesAgo(community.getCreatedAt()),
                isModified,
                isAuthor
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