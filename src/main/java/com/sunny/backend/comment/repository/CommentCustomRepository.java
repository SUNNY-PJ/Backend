package com.sunny.backend.comment.repository;

import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.comment.domain.Comment;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;

public interface CommentCustomRepository {
    Optional<Comment> findCommentByIdWithParent(Long id);

}
