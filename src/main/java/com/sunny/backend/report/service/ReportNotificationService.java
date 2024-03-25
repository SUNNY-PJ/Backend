package com.sunny.backend.report.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportNotificationService {
	private final NotificationService notificationService;
	private final NotificationRepository notificationRepository;

	public void sendNotifications(Users users) {
		Long postAuthor = users.getId();
		String title = "[SUNNY]";
		String bodyTitle = users.getReportCount() + "번 째 경고를 받았어요";
		String body = users.getReportCount() + "번 째 경고를 받았어요";
		List<Notification> notificationList = notificationRepository.findByUsers_Id(users.getId());

		if (notificationList.size() != 0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				bodyTitle,
				body
			);
			notificationService.sendNotificationToFriends(title, notificationPushRequest);
		}
	}
}
