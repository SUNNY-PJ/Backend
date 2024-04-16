package com.sunny.backend.notification.dto.response;

import static com.sunny.backend.notification.domain.NotificationType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.notification.domain.NotifiacationSubType;
import com.sunny.backend.notification.domain.NotificationType;
import com.sunny.backend.user.domain.Users;

public record AlarmListResponse(
	String alarmId,
	Long id,
	Long userId,
	String postAuthor,
	String title,
	String notificationContent,
	String profileImg,
	NotificationType notificationType,
	NotifiacationSubType subType,
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
			commentNotification.getCommunity().getId(),
			commentNotification.getUsers().getId(),
			commentUser.getNickname(),
			commentNotification.getTitle(),
			comment.getContent(),
			commentUser.getProfile(),
			COMMENT,
			NotifiacationSubType.REGISTER,
			isToday,
			comment.getCreatedDate()
		);
	}

	public static AlarmListResponse fromFriendsAlert(FriendsNotification friendsNotification) {
		return new AlarmListResponse(
			UUID.randomUUID().toString(),
			friendsNotification.getFriendId(), //상대방꺼 id
			friendsNotification.getUsers().getId(),
			friendsNotification.getFriend().getNickname(),
			friendsNotification.getTitle(),
			friendsNotification.getFriend().getNickname() + friendsNotification.getBody(),
			friendsNotification.getFriend().getProfile(),
			FRIEND,
			friendsNotification.getSubType(),
			friendsNotification.getCreatedDate().toLocalDate().isEqual(LocalDate.now()),
			friendsNotification.getCreatedDate()
		);
	}

	public static AlarmListResponse fromCompetitionAlert(CompetitionNotification competitionNotification) {
		return new AlarmListResponse(
			UUID.randomUUID().toString(),
			competitionNotification.getFriendCompetition().getCompetition().getId(),
			competitionNotification.getFriendCompetition().getFriend().getId(),
			competitionNotification.getFriend().getNickname(),
			competitionNotification.getTitle(),
			competitionNotification.getFriend().getNickname() + competitionNotification.getBody(),
			competitionNotification.getFriend().getProfile(),
			COMPETITION,
			competitionNotification.getSubType(),
			competitionNotification.getCreatedDate().toLocalDate().isEqual(LocalDate.now()),
			competitionNotification.getCreatedDate()
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
