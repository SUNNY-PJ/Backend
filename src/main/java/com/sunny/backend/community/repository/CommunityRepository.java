package com.sunny.backend.community.repository;

import static com.sunny.backend.comment.exception.CommentErrorCode.*;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long>,
    CommunityRepositoryCustom {
    List<Community> findAllByUsers_Id(Long userId);
    default Community getById(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
    }
}
