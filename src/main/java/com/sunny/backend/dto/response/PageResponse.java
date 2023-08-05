package com.sunny.backend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Photo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PageResponse {
    private Long id; //게시글 일련번호 안보여주면 삭제
    private String title;
    private String writer;
    private int view_cnt;
    private int comment_cnt; //댓글 수
    //+ 내용, 등록일자,미디어 미리보기 등은 따로 추가 안하는지?

    public PageResponse (Community community){
        this.id=community.getId();
        this.title=community.getTitle();
        this.writer=community.getWriter();
        this.view_cnt=community.getView_cnt();
        this.comment_cnt=community.getCommentList().size(); //사이즈 이렇게 두는 거 맞는지?


    }
}
