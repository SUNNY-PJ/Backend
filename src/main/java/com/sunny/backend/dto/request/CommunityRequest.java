package com.sunny.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommunityRequest {
    private String title; //제목
    private String contents; //내용
    private String writer; //작성자, 유저 관계 성립시 삭제
    //글 쓰는 방식에 따라 카테고리 request가 달라질 듯
    //이미지는 RequestPart 형식
}
