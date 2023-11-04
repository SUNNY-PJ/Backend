package com.sunny.backend.dto.response.community;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Photo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class CommunityResponse {
    //제목, 작성자, 등록일 , 등록 시간, 조회수 , 내용 , 댓글 리스트, 비밀 댓글

    private Long id;
    private String title; //제목
    private String contents; //내용
    private String writer; //작성자
    private int viewCount; // 조회수
    private List<String> photoList; // 이미지 리스트
    private List<CommentResponse> commentList; //댓글 리스트

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime updateDate;

    public CommunityResponse(Community community) {
        this.id=community.getId();
        this.writer = community.getWriter();
        this.title = community.getTitle();
        this.contents = community.getContents();
        this.viewCount = community.getView_cnt();
        this.createdDate = community.getCreatedDate();
        this.updateDate=community.getUpdatedDate();
        this.photoList = community.getPhotoList()
                .stream()
                .map(Photo::getFileUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        this.commentList = community.getCommentList()
                .stream()
                .filter(comment -> comment.getParent() == null)
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse(
                comment.getId(),
                comment.getWriter(), // 필드에 따라 업데이트
                comment.getContent()
        );

        commentResponse.setChildren(comment.getChildren()
                .stream()
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList())
        );

        return commentResponse;
    }

    @Getter
    public static class PageResponse {
        private Long id;
        //제목, 작성자, 조회수 , 댓글수
        private String title; //제목
        private String writer; //작성자
        private int view_cnt; //조회수
        private int comment_cnt; //댓글 수

        public PageResponse(Community community) {
            this.id=community.getId();
            this.title = community.getTitle();
            this.writer = community.getWriter();
            this.view_cnt = community.getView_cnt();
            this.comment_cnt = community.getCommentList().size();
        }
    }


}
