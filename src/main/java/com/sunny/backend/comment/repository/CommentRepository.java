package com.sunny.backend.comment.repository;

import static com.sunny.backend.common.CommonErrorCode.*;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.CommonCustomException;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long>, CommentCustomRepository {
    List<Comment> findAllByUsers_Id (Long userId);
    List<Comment> findAllByCommunity_Id (Long communityId);

    default Comment getById(Long id) {
        return findById(id)
            .orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));
    }
}
