package com.sunny.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommentRequest {
    private Long userId; //유저 아이디
    private Long parentId; //부모 아이디
    private String content; //댓글 내용

    public CommentRequest(String content){
        this.content=content;
    }
}
