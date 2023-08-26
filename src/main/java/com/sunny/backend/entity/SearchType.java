package com.sunny.backend.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchType {
    // 글 제목
    private String title;

    // 글 내용
    private String content;

    //글 작성자
    private String writer;
    private BoardType boardType = BoardType.꿀팁;

}
