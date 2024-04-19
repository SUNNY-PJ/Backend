package com.sunny.backend.report.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.notification.domain.NotifiacationSubType;
import com.sunny.backend.notification.service.FriendNotiService;
import com.sunny.backend.report.domain.CommunityReport;
import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.report.repository.CommunityReportRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.dto.response.ReportResponse;
import com.sunny.backend.user.dto.response.UserReportResponse;
import com.sunny.backend.user.dto.response.UserReportResultResponse;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityReportService implements ReportStrategy {
	private final SimpMessagingTemplate template;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;
	private final CommunityReportRepository communityReportRepository;
	private final ReportNotificationService reportNotificationService;
	private final FriendNotiService friendNotiService;

	@Override
	public UserReportResponse report(Long userId, ReportCreateRequest reportCreateRequest) {
		Users users = userRepository.getById(userId);
		Community community = communityRepository.getById(reportCreateRequest.id());
		CommunityReport communityReport = CommunityReport.of(
			users,
			community,
			reportCreateRequest.reason()
		);
		communityReportRepository.save(communityReport);
		return UserReportResponse.fromCommunityReport(communityReport);
	}

	@Override
	public void approveUserReport(Long communityId) {
		CommunityReport communityReport = communityReportRepository.getById(communityId);
		communityReport.validateWaitStatus();
		communityReport.approveStatus();

		Users reportUsers = communityReport.getUsers();
		Users users = communityReport.getCommunity().getUsers();
		users.increaseReportCount();

		//신고 승인 된 경우
		String reportUserBodyTitle = "신고 결과를 알려드려요.";
		String UserBodyTitle = "신고가 접수되었어요.";
		String reportUserBody = users.getReportCount() + "번째 경고를 받았습니다.";
		String body = "회원님의 신고에 대한 결과를 알려드려요";
		String cotents = communityReport.getCommunity().getContents();
		String reasonContent = communityReport.getReason();
		String reportContent = "부적절한 컨텐츠를 포함하고 있습니다";

		reportNotificationService.sendUserReportNotifications(reportUserBodyTitle, body, reportUserBodyTitle,
			cotents,
			reasonContent,
			reportUsers, //신고한 사람
			users,
			NotifiacationSubType.APPROVE, communityReport.getCreatedDate()); //신고 한 사람
		reportNotificationService.sendUserReportNotifications(reportUserBodyTitle, reportUserBody, UserBodyTitle,
			cotents,
			reportContent,
			users, //신고한 사람
			reportUsers,
			NotifiacationSubType.WARN, communityReport.getCreatedDate()); //신고 한 사람

		template.convertAndSend("/sub/user/" + reportUsers.getId(),
			UserReportResultResponse.ofCommunity(communityReport, true));

		if (users.isReportLimitReached()) {
			userRepository.deleteById(users.getId());
		}
	}

	@Override
	public void refuseUserReport(Long communityId) {
		CommunityReport communityReport = communityReportRepository.getById(communityId);
		communityReport.validateWaitStatus();
		Users reportUsers = communityReport.getUsers();
		//신고 거절 된 경우
		String reportUserBodyTitle = "신고 결과를 알려드려요";
		String body = "회원님의 신고에 대한 결과를 알려드려요";
		String cotents = communityReport.getCommunity().getContents();
		String reportContent = communityReport.getReason(); //신고 사유
		Users users = communityReport.getCommunity().getUsers();
		reportNotificationService.sendUserReportNotifications(reportUserBodyTitle, body, reportUserBodyTitle, cotents,
			reportContent,
			reportUsers, //신고한 사람
			users,
			NotifiacationSubType.REFUSE, communityReport.getCreatedDate()); //신고 한 사람

		communityReportRepository.deleteById(communityId);
		template.convertAndSend("/sub/user/" + communityReport.getUsers().getId(),
			UserReportResultResponse.ofCommunity(communityReport, false));
	}

	@Override
	public List<ReportResponse> getUserReports(ReportType reportType) {
		return communityReportRepository.findAll()
			.stream()
			.map(ReportResponse::fromCommunityReport)
			.toList();
	}

	@Override
	public ReportType getReportType() {
		return ReportType.COMMUNITY;
	}

}
