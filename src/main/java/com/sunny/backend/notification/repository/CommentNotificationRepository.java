package com.sunny.backend.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.user.domain.Users;

public interface CommentNotificationRepository extends JpaRepository<CommentNotification, Long> {

	List<CommentNotification> findByCommunityId(Long communityId);

	List<CommentNotification> findByUsers_Id(Long userId);

	List<CommentNotification> findByComment(Comment comment);

	List<CommentNotification> findByCreatedAtAfter(LocalDateTime createdAt);

	void deleteAllByUsers(Users users);

}