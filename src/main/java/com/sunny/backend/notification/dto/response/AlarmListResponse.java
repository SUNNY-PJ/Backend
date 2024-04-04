package com.sunny.backend.notification.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.notification.domain.FriendsNotification;

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
	public static AlarmListResponse from(FriendsNotification friendsNotification) {
		return new AlarmListResponse(
				UUID.randomUUID().toString(),
				friendsNotification.getFriend().getId(), //상대방꺼 id
				friendsNotification.getFriend().getNickname(),
				friendsNotification.getTitle(),
				friendsNotification.getFriend().getNickname()+friendsNotification.getBody(),
				friendsNotification.getFriend().getProfile(),
				friendsNotification.getCreatedDate().toLocalDate().isEqual(LocalDate.now()),
				friendsNotification.getCreatedDate()
		);
	}
}
