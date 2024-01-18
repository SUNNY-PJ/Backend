package com.sunny.backend.comment.repository;

import com.sunny.backend.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long>, CommentCustomRepository {

    List<Comment> findAllByUsers_Id (Long userId);
    List<Comment> findAllByCommunity_Id (Long communityId);
}
