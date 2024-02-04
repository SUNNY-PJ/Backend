package com.sunny.backend.declaration.repository;

import static com.sunny.backend.declaration.exception.DeclarationErrorCode.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.declaration.domain.CommentDeclaration;

public interface CommentDeclarationRepository extends JpaRepository<CommentDeclaration, Long> {
	default CommentDeclaration getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(DECLARE_COMMENT_NOT_FOUND));
	}
}
