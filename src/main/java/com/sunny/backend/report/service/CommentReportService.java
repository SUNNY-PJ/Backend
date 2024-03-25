package com.sunny.backend.report.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.report.domain.CommentReport;
import com.sunny.backend.report.domain.ReportStatus;
import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.report.dto.ReportRequest;
import com.sunny.backend.report.repository.CommentReportRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.dto.response.UserReportResponse;
import com.sunny.backend.user.dto.response.UserReportResultResponse;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentReportService implements ReportService {
	private final SimpMessagingTemplate template;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final CommentReportRepository commentReportRepository;
	private final ReportNotificationService reportNotificationService;

	@Override
	public UserReportResponse report(CustomUserPrincipal customUserPrincipal, ReportCreateRequest reportCreateRequest) {
		Comment comment = commentRepository.getById(reportCreateRequest.id());
		CommentReport commentReport = CommentReport.builder()
			.users(customUserPrincipal.getUsers())
			.comment(comment)
			.reason(reportCreateRequest.reason())
			.status(ReportStatus.WAIT)
			.build();
		commentReportRepository.save(commentReport);
		return UserReportResponse.fromCommentReport(commentReport);
	}

	@Override
	public void approveUserReport(ReportRequest reportRequest) {
		CommentReport commentReport = commentReportRepository.getById(reportRequest.id());
		commentReport.isWait();
		commentReport.approveStatus();

		Users reportUsers = commentReport.getUsers();
		Users users = commentReport.getComment().getUsers();
		if (users.getReportCount() == 4) {
			userRepository.deleteById(users.getId());
		} else {
			users.increaseReportCount();
			reportNotificationService.sendNotifications(users);
		}
		template.convertAndSend("/sub/user/" + reportUsers.getId(),
			UserReportResultResponse.ofCommentReport(commentReport, true));
	}

	@Override
	public void refuseUserReport(ReportRequest reportRequest) {
		CommentReport commentReport = commentReportRepository.getById(reportRequest.id());
		commentReport.isWait();
		commentReportRepository.deleteById(reportRequest.id());
		template.convertAndSend("/sub/user/" + commentReport.getUsers().getId(),
			UserReportResultResponse.ofCommentReport(commentReport, false));
	}

	@Override
	public boolean isReportType(ReportType reportType) {
		return reportType == ReportType.COMMENT;
	}
}
