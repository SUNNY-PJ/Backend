package com.sunny.backend.report.service;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.dto.response.ReportResponse;
import com.sunny.backend.user.dto.response.UserReportResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportFactory reportFactory;
	private final NotificationRepository notificationRepository;
	private final NotificationService notificationService;

	public List<ReportResponse> getUserReports(ReportType reportType) {
		ReportStrategy reportStrategy = reportFactory.findReportStrategy(reportType);
		return reportStrategy.getUserReports(reportType);
	}

	public UserReportResponse createUserReport(
		CustomUserPrincipal customUserPrincipal,
		ReportCreateRequest reportCreateRequest
	) {
		ReportStrategy reportStrategy = reportFactory.findReportStrategy(reportCreateRequest.reportType());
		return reportStrategy.report(customUserPrincipal.getId(), reportCreateRequest);
	}

	//승인
	@Transactional
	public void approveUserReport(Long id, ReportType reportType) {
		ReportStrategy reportStrategy = reportFactory.findReportStrategy(reportType);
		reportStrategy.approveUserReport(id);

	}

	@Transactional
	public void refuseUserReport(Long id, ReportType reportType) {
		ReportStrategy reportStrategy = reportFactory.findReportStrategy(reportType);
		reportStrategy.refuseUserReport(id);
	}

	private void reportResultNotifications(Users users) throws IOException {
		Long postAuthor = users.getId();
		String title = "[SUNNY]";
		String bodyTitle = "신고 결과를 알려드려요";
		String body = "회원님의 신고에 대한 결과를 알려드려요";
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
