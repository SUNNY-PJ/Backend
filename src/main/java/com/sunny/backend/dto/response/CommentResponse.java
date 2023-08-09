package com.sunny.backend.dto.response;

import com.sunny.backend.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class CommentResponse {
    private Long id;
    private String content;
    private String writer;
    private List<CommentResponse> children = new ArrayList<>();

    public CommentResponse(Long id, String content, String writer) {
        this.id = id;
        this.writer = writer;
        this.content = content;

    }
    //삭제된 댓글로 댓글 내용 수정하기 위한 객체 생성
    public static CommentResponse convertCommentToDto(Comment comment) {
        return comment.getIsDeleted() ?
                new CommentResponse(comment.getId(), "삭제된 댓글입니다.", null) :
                new CommentResponse(comment.getId(), comment.getContent(), comment.getUsers().getName());
    }
}
