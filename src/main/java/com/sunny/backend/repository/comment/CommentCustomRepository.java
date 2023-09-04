package com.sunny.backend.repository.comment;

import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentCustomRepository {
    Optional<Comment> findCommentByIdWithParent(Long id);
    List<CommentResponse> findByCommunityId(Long id);
}
