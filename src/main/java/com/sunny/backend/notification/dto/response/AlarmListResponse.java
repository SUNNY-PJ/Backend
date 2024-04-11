package com.sunny.backend.notification.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.user.domain.Users;

public record AlarmListResponse(
	String alarmId,
	Long id,
	String postAuthor,
	String title,
	String notificationContent,
	String profileImg,

	boolean isToday,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime createdAt
) {
	public static AlarmListResponse fromCommentAlert(CommentNotification commentNotification) {
		Comment comment = commentNotification.getComment();
		Users commentUser = comment.getUsers();
		boolean isToday = comment.getCreatedDate().toLocalDate().isEqual(LocalDate.now());

		return new AlarmListResponse(
			UUID.randomUUID().toString(),
			commentNotification.getId(),
			commentUser.getNickname(),
			commentNotification.getTitle(),
			comment.getContent(),
			commentUser.getProfile(),
			isToday,
			comment.getCreatedDate()
		);
	}

	public static AlarmListResponse fromFriendsAlert(FriendsNotification friendsNotification) {
		return new AlarmListResponse(
			UUID.randomUUID().toString(),
			friendsNotification.getFriend().getId(), //상대방꺼 id
			friendsNotification.getFriend().getNickname(),
			friendsNotification.getTitle(),
			friendsNotification.getFriend().getNickname() + friendsNotification.getBody(),
			friendsNotification.getFriend().getProfile(),
			friendsNotification.getCreatedDate().toLocalDate().isEqual(LocalDate.now()),
			friendsNotification.getCreatedDate()
		);
	}

	public static List<AlarmListResponse> fromCommentNotifications(List<CommentNotification> commentNotifications,
		Long currentUserId) {
		return commentNotifications.stream()
			.filter(notification -> {
				Comment comment = notification.getComment();
				return comment != null && comment.getUsers() != null &&
					!comment.getIsDeleted() &&
					!comment.getUsers().getId().equals(currentUserId);
			})
			.map(AlarmListResponse::fromCommentAlert)
			.toList();
	}
}
