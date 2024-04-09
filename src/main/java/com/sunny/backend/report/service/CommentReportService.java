package com.sunny.backend.report.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.report.domain.CommentReport;
import com.sunny.backend.report.domain.ReportType;
import com.sunny.backend.report.dto.ReportCreateRequest;
import com.sunny.backend.report.repository.CommentReportRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.dto.response.ReportResponse;
import com.sunny.backend.user.dto.response.UserReportResponse;
import com.sunny.backend.user.dto.response.UserReportResultResponse;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentReportService implements ReportStrategy {
	private final SimpMessagingTemplate template;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final CommentReportRepository commentReportRepository;
	private final ReportNotificationService reportNotificationService;

	@Override
	public UserReportResponse report(Long userId, ReportCreateRequest reportCreateRequest) {
		Users users = userRepository.getById(userId);
		Comment comment = commentRepository.getById(reportCreateRequest.id());
		CommentReport commentReport = CommentReport.of(
			users,
			comment,
			reportCreateRequest.reason()
		);
		commentReportRepository.save(commentReport);
		return UserReportResponse.fromCommentReport(commentReport);
	}

	@Override
	public void approveUserReport(Long commentId) {
		CommentReport commentReport = commentReportRepository.getById(commentId);
		commentReport.validateWaitStatus();
		commentReport.approveStatus();

		Users reportUsers = commentReport.getUsers();
		Users users = commentReport.getComment().getUsers();
		users.increaseReportCount();

		reportNotificationService.sendNotifications(users);

		template.convertAndSend("/sub/user/" + reportUsers.getId(),
			UserReportResultResponse.ofCommentReport(commentReport, true));

		if (users.isReportLimitReached()) {
			userRepository.deleteById(users.getId());
		}
	}

	@Override
	public void refuseUserReport(Long commentId) {
		CommentReport commentReport = commentReportRepository.getById(commentId);
		commentReport.validateWaitStatus();
		commentReportRepository.deleteById(commentId);
		template.convertAndSend("/sub/user/" + commentReport.getUsers().getId(),
			UserReportResultResponse.ofCommentReport(commentReport, false));
	}

	@Override
	public List<ReportResponse> getUserReports(ReportType reportType) {
		return commentReportRepository.findAll()
			.stream()
			.map(ReportResponse::fromCommentReport)
			.toList();
	}

	@Override
	public ReportType getReportType() {
		return ReportType.COMMENT;
	}

}
