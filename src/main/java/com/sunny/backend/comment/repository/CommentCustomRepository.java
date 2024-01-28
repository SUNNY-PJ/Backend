package com.sunny.backend.comment.repository;

import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentCustomRepository {
    Optional<Comment> findCommentByIdWithParent(Long id);
//    List<CommentResponse> findByCommunityId(Long id);
}
