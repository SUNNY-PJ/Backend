package com.sunny.backend.report.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sunny.backend.notification.domain.NotifiacationSubType;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.domain.UserReportNotification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.repository.UserReportNotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportNotificationService {
	private final NotificationService notificationService;
	private final NotificationRepository notificationRepository;
	private final UserReportNotificationRepository userReportNotificationRepository;

	//신고 한 사람
	public void sendUserNotifications(String title, String body, String bodyTitle, String content,
		String reportContnet,
		Users reportUsers, Users users,
		NotifiacationSubType subType,
		LocalDateTime createdAt) {
		Long postAuthor = reportUsers.getId();
		UserReportNotification userReportNotification = UserReportNotification.builder()
			.users(reportUsers) //신고한 사람
			.warnUser(users) // 신고 받은 사람
			.title(bodyTitle)
			.body(body)
			.content(content)
			.reportContent(reportContnet)
			.subType(subType)
			.reportCreatedAt(createdAt)
			.createdAt(LocalDateTime.now())
			.build();
		userReportNotificationRepository.save(userReportNotification);
		List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);
		if (notificationList.size() != 0) {
			String notificationBody = "신고 결과를 알려드려요";
			String notificationBodyTitle = "써니";
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				notificationBody,
				notificationBodyTitle
			);
			notificationService.sendNotificationToFriends(notificationBodyTitle, notificationPushRequest);
		}
	}

	public void sendUserReportNotifications(String title, String body, String bodyTitle, String content,
		String reportContnet,
		Users reportUsers, Users users,
		NotifiacationSubType subType,
		LocalDateTime createdAt) {
		Long postAuthor = users.getId();
		UserReportNotification userReportNotification = UserReportNotification.builder()
			.users(users) //신고한 사람
			.warnUser(reportUsers) // 신고 받은 사람
			.title(bodyTitle)
			.body(body)
			.content(content)
			.reportContent(reportContnet)
			.subType(subType)
			.reportCreatedAt(createdAt)
			.createdAt(LocalDateTime.now())
			.build();
		userReportNotificationRepository.save(userReportNotification);
		List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);
		if (notificationList.size() != 0) {
			String notificationBody = users.getReportCount() + "번째 경고를 받았습니다.";
			String notificationBodyTitle = "써니";
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				notificationBody,
				notificationBodyTitle
			);
			notificationService.sendNotificationToFriends(notificationBodyTitle, notificationPushRequest);
		}
	}

	public void sendNotifications(Users users) { //신고 받은 사람
		Long postAuthor = users.getId();
		String title = "[SUNNY]";
		String bodyTitle = users.getReportCount() + "번째 경고를 받았습니다.";
		String body = users.getReportCount() + "번째 경고를 받았습니다.";
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
