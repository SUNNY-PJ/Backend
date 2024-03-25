package com.sunny.backend.report.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.report.domain.CommunityReport;
import com.sunny.backend.report.domain.ReportStatus;
import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.report.dto.ReportRequest;
import com.sunny.backend.report.repository.CommunityReportRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.dto.response.UserReportResponse;
import com.sunny.backend.user.dto.response.UserReportResultResponse;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityReportService implements ReportService {
	private final SimpMessagingTemplate template;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;
	private final CommunityReportRepository communityReportRepository;
	private final ReportNotificationService reportNotificationService;

	@Override
	public UserReportResponse report(CustomUserPrincipal customUserPrincipal, ReportCreateRequest reportCreateRequest) {
		Community community = communityRepository.getById(reportCreateRequest.id());
		CommunityReport communityReport = CommunityReport.builder()
			.users(customUserPrincipal.getUsers())
			.community(community)
			.reason(reportCreateRequest.reason())
			.status(ReportStatus.WAIT)
			.build();
		communityReportRepository.save(communityReport);
		return UserReportResponse.fromCommunityReport(communityReport);
	}

	@Override
	public void approveUserReport(ReportRequest reportRequest) {
		CommunityReport communityReport = communityReportRepository.getById(reportRequest.id());
		communityReport.isWait();
		communityReport.approveStatus();

		Users reportUsers = communityReport.getUsers();
		Users users = communityReport.getCommunity().getUsers();
		if (users.getReportCount() == 4) {
			userRepository.deleteById(users.getId());
		} else {
			users.increaseReportCount();
			reportNotificationService.sendNotifications(users);
		}
		template.convertAndSend("/sub/user/" + reportUsers.getId(),
			UserReportResultResponse.ofCommunity(communityReport, true));
	}

	@Override
	public void refuseUserReport(ReportRequest reportRequest) {
		CommunityReport communityReport = communityReportRepository.getById(reportRequest.id());
		communityReport.isWait();
		communityReportRepository.deleteById(reportRequest.id());
		template.convertAndSend("/sub/user/" + communityReport.getUsers().getId(),
			UserReportResultResponse.ofCommunity(communityReport, false));
	}

	@Override
	public boolean isReportType(ReportType reportType) {
		return reportType == ReportType.COMMUNITY;
	}
}
