package com.sunny.backend.report.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
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

	@Override
	public UserReportResponse report(Users users, ReportCreateRequest reportCreateRequest) {
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

		reportNotificationService.sendNotifications(users);

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
