package com.sunny.backend.dto.response.community;

import com.sunny.backend.util.DatetimeUtil;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.common.photo.Photo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public record CommunityResponse(
    Long id,
    String writer,
    String title,
    String contents,
    int viewCount,
    List<String> photoList,
    int commentCnt,
    BoardType type,
    String profileImg,
    String createdAt,
    String modifiedAt,
    boolean isModified,
    boolean isScraped
) {

    public static CommunityResponse of(Community community, boolean isModified, boolean isScraped) {
        return new CommunityResponse(
            community.getId(),
            community.getUsers().getName(),
            community.getTitle(),
            community.getContents(),
            community.getView_cnt(),
            community.getPhotoList()
                .stream()
                .map(Photo::getFileUrl)
                .filter(Objects::nonNull)
                .toList(),
            community.getCommentList().size(),
            community.getBoardType(),
            community.getUsers().getProfile(),
            DatetimeUtil.timesAgo(community.getCreatedDate()),
            isModified ? DatetimeUtil.timesAgo(LocalDateTime.now())
                : DatetimeUtil.timesAgo(community.getUpdatedDate()),
            isModified,
            isScraped
        );
    }


    public record PageResponse(
        Long id,
        String title,
        String writer,
        int viewCount,
        int commentCount,
        String createdAt,
        String modifiedAt
    ) {

        public static PageResponse from(Community community) {
            return new PageResponse(
                community.getId(),
                community.getTitle(),
                community.getUsers().getName(),
                community.getView_cnt(),  // Corrected method name
                community.getCommentList().size(),
                DatetimeUtil.timesAgo(community.getCreatedDate()),
                DatetimeUtil.timesAgo(
                    community.getUpdatedDate() != null ? community.getUpdatedDate()
                        : community.getCreatedDate())
            );
        }
    }
}