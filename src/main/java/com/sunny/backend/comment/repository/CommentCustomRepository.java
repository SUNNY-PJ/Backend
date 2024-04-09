package com.sunny.backend.comment.repository;

import java.util.Optional;

import com.sunny.backend.comment.domain.Comment;

public interface CommentCustomRepository {
	Optional<Comment> findCommentByIdWithParent(Long id);

}
