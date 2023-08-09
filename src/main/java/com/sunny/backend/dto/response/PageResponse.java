package com.sunny.backend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Photo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PageResponse {
    //제목, 작성자, 조회수 , 댓글수
    private String title; //제목
    private String writer; //작성자
    private int view_cnt; //조회수
    private int comment_cnt; //댓글 수


    public PageResponse(Community community) {

        this.title = community.getTitle();
        this.writer = community.getWriter();
        this.view_cnt = community.getView_cnt();
        this.comment_cnt = community.getCommentList().size();
    }

}
