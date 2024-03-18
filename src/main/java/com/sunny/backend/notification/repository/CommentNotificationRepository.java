package com.sunny.backend.notification.repository;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.notification.domain.CommentNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentNotificationRepository extends JpaRepository<CommentNotification, Long> {

  List<CommentNotification> findByCommunityId(Long communityId);

  List<CommentNotification> findByUsers_Id(Long userId);

  List<CommentNotification> findByComment(Comment comment);

  List<CommentNotification> findByCreatedDateAfter(LocalDateTime createdDate);
  void deleteByUsersId(Long userId);

}