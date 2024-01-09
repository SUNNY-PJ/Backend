package com.sunny.backend.community.dto;

import com.sunny.backend.common.DatetimeUtil;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.entity.Photo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Getter
public class CommunityResponse {

    private Long id;
    private String title;
    private String contents;
    private String writer;
    private int viewCount;
    private List<String> photoList;
    private int comment_cnt;
    private BoardType type;
    private String profileImg;
    private String createdAt;
    private String modifiedAt;
    private boolean isModified;

    public CommunityResponse(Community community, boolean isModified) {
        this.id = community.getId();
        this.writer = community.getUsers().getName();
        this.title = community.getTitle();
        this.contents = community.getContents();
        this.viewCount = community.getView_cnt();
        this.photoList = community.getPhotoList()
            .stream()
            .map(Photo::getFileUrl)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        this.comment_cnt = community.getCommentList().size();
        this.profileImg=community.getUsers().getProfile();
        this.createdAt =DatetimeUtil.timesAgo(community.getCreatedDate());
        this.isModified=isModified;
        //수정된 값이 null : 수정을 아직 안함 ->  수정된 값은 createdAt 업데이트
        if(isModified){
            this.modifiedAt =DatetimeUtil.timesAgo(LocalDateTime.now());
        } else{
            this.modifiedAt=DatetimeUtil.timesAgo(community.getUpdatedDate());
        }
        this.type=community.getBoardType();
    }

    @Getter
    public static class PageResponse {

        private Long id;
        //제목, 작성자, 조회수 , 댓글수
        private String title; //제목
        private String writer; //작성자
        private int view_cnt; //조회수
        private int comment_cnt; //댓글 수
        private String createdAt; // 등록
        private String modifiedAt; // 등록

        public PageResponse(Community community) {
            this.id=community.getId();
            this.title = community.getTitle();
            this.writer = community.getUsers().getName();
            this.view_cnt = community.getView_cnt();
            this.comment_cnt = community.getCommentList().size();
            this.createdAt = DatetimeUtil.timesAgo(community.getCreatedDate());
            this.modifiedAt = DatetimeUtil.timesAgo(community.getUpdatedDate() != null ? community.getUpdatedDate() : community.getCreatedDate());

        }
    }
}