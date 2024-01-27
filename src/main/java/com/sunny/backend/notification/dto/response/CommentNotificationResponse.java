package com.sunny.backend.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.notification.domain.CommentNotification;
import java.time.LocalDateTime;
import java.util.List;


public record CommentNotificationResponse (
   Long id,
   Long communityId,
   String postAuthor,
   String title,
   String NotificationContent,
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
   LocalDateTime createdAt
    ){ public static List<CommentNotificationResponse> listOf(
      List<CommentNotification> commentNotifications) {
    String title="새로운 댓글이 달렸어요";
    return commentNotifications.stream()
        .map(commentNotification -> new CommentNotificationResponse(
            commentNotification.getId(),
            commentNotification.getCommunity().getId(),
            commentNotification.getComment().getUsers().getName(),
            title,
            commentNotification.getComment().getContent(),
            commentNotification.getComment().getCreatedDate()
        ))
       .toList();
  }
}