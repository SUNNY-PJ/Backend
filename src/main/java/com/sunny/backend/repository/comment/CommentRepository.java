package com.sunny.backend.repository.comment;

import com.sunny.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long>, CommentCustomRepository {
}
