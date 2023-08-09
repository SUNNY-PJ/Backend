package com.sunny.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Photo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter // 삭제
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommunityResponse {
    //제목, 작성자, 등록일 , 등록 시간, 조회수 , 내용 , 댓글 리스트, 비밀 댓글
    private String title; //제목
    private String contents; //내용
    private String writer; //작성자
    private int viewCount; // 조회수
    private List<String> photoList; // 이미지 리스트
    private List<CommentResponse> commentList; //댓글 리스트

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updateDate;

    public CommunityResponse(Community community) {
        this.writer = community.getWriter();
        this.title = community.getTitle();
        this.contents = community.getContents();
        this.viewCount = community.getView_cnt();
        this.createdDate = community.getCreatedDate();
        this.updateDate=community.getUpdatedDate();
        this.photoList = new ArrayList<>();
        List<Photo> photoList = community.getPhotoList();
        if (photoList != null) {
            for (Photo photo : photoList) {
                String fileUrl = photo.getFileUrl();
                if (fileUrl != null) {
                    this.photoList.add(fileUrl);
                }
            }
        }

        this.commentList = new ArrayList<>();
        List<Comment> commentList = community.getCommentList();
        if (commentList != null) {
            for (Comment comment : commentList) {
                if (comment.getParent() == null) {
                    CommentResponse commentResponse = mapCommentToResponse(comment);
                    this.commentList.add(commentResponse);
                }
            }
        }
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse(
                comment.getId(),
                comment.getWriter(), // 필드에 따라 업데이트
                comment.getContent()
        );

        for (Comment childComment : comment.getChildren()) {
            CommentResponse childCommentResponse = mapCommentToResponse(childComment);
            commentResponse.getChildren().add(childCommentResponse);
        }

        return commentResponse;
    }

}
