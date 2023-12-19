package com.sunny.backend.repository.comment;

import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long>, CommentCustomRepository {

    List<Comment> findAllByUsers_Id (Long userId);
}
