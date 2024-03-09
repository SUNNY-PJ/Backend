package com.sunny.backend.user.service;

import static com.sunny.backend.common.ComnConstant.*;

import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.friends.domain.Status;
import com.sunny.backend.report.domain.CommentReport;
import com.sunny.backend.report.domain.CommunityReport;
import com.sunny.backend.report.dto.ReportRequest;
import com.sunny.backend.report.dto.ReportStatusRequest;
import com.sunny.backend.report.repository.CommentReportRepository;
import com.sunny.backend.report.repository.CommunityReportRepository;
import com.sunny.backend.scrap.repository.ScrapRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.dto.response.ProfileResponse;
import com.sunny.backend.user.dto.response.UserCommentResponse;
import com.sunny.backend.user.dto.response.UserCommunityResponse;
import com.sunny.backend.user.dto.response.UserReportResponse;
import com.sunny.backend.user.dto.response.UserReportResultResponse;
import com.sunny.backend.user.dto.response.UserScrapResponse;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.S3Util;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;
	private final CommentRepository commentRepository;
	private final ResponseService responseService;
	private final ScrapRepository scrapRepository;
	private final CommunityReportRepository communityReportRepository;
	private final CommentReportRepository commentReportRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationService notificationService;
	private final SimpMessagingTemplate template;
	private final S3Util s3Util;

	public Users checkUserId(CustomUserPrincipal customUserPrincipal, Long userId) {
		if (userId != null) {
			return userRepository.getById(userId);
		}
		return customUserPrincipal.getUsers();
	}

	@Transactional(readOnly = true)
	public ProfileResponse getUserProfile(CustomUserPrincipal customUserPrincipal, Long userId) {
		Users user = checkUserId(customUserPrincipal, userId);
		return ProfileResponse.of(user, customUserPrincipal.getUsers().getId().equals(userId));
	}

	@Transactional(readOnly = true)
	public List<UserCommunityResponse> getUserCommunityList(CustomUserPrincipal customUserPrincipal, Long userId) {
		Users user = checkUserId(customUserPrincipal, userId);

		return communityRepository.findAllByUsers_Id(user.getId())
			.stream()
			.map(UserCommunityResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<UserCommentResponse> getCommentByUserId(CustomUserPrincipal customUserPrincipal, Long userId) {
		Users user = checkUserId(customUserPrincipal, userId);

		return commentRepository.findAllByUsers_Id(user.getId())
			.stream()
			.map(UserCommentResponse::from)
			.toList();
	}

	public List<UserScrapResponse> getScrapList(CustomUserPrincipal customUserPrincipal) {
		return scrapRepository.findAllByUsers_Id(customUserPrincipal.getUsers().getId())
			.stream()
			.map(scrap -> UserScrapResponse.from(scrap.getCommunity()))
			.toList();
	}

	public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(
		CustomUserPrincipal customUserPrincipal, MultipartFile profile) {

		Users user = customUserPrincipal.getUsers();
		// 새 프로필 업로드
		if (profile != null && !profile.isEmpty()) {
			String uploadedProfileUrl = s3Util.upload(profile);
			user.updateProfile(uploadedProfileUrl);
		} else if (profile == null) {
			user.updateProfile(SUNNY_DEFAULT_IMAGE);
		}
		userRepository.save(user);
		ProfileResponse profileResponse = ProfileResponse.from(user);
		return responseService.getSingleResponse(HttpStatus.OK.value(), profileResponse, "프로필 변경 완료");
	}

	public UserReportResponse reportCommunity(CustomUserPrincipal customUserPrincipal, ReportRequest reportRequest) {
		switch (reportRequest.status()) {
			case COMMUNITY -> {
				Community community = communityRepository.getById(reportRequest.id());
				CommunityReport communityReport = CommunityReport.builder()
					.users(customUserPrincipal.getUsers())
					.community(community)
					.reason(reportRequest.reason())
					.status(Status.WAIT)
					.build();
				communityReportRepository.save(communityReport);
				return UserReportResponse.toCommunity(communityReport.getCreatedDate(), community,
					reportRequest.reason());
			}
			case COMMENT -> {
				Comment comment = commentRepository.getById(reportRequest.id());
				CommentReport commentReport = CommentReport.builder()
					.users(customUserPrincipal.getUsers())
					.comment(comment)
					.reason(reportRequest.reason())
					.status(Status.WAIT)
					.build();
				commentReportRepository.save(commentReport);
				return UserReportResponse.toComment(commentReport.getCreatedDate(), comment, reportRequest.reason());
			}
		}
		return null;
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> approveUserReport(ReportStatusRequest reportStatusRequest)
			throws IOException {
		switch (reportStatusRequest.status()) {
			case COMMUNITY -> {
				CommunityReport communityReport = communityReportRepository.getById(reportStatusRequest.id());
				communityReport.isWait();
				communityReport.approveStatus();

				Users report = communityReport.getUsers();
				Users users = communityReport.getCommunity().getUsers();
				if (users.getReportCount() == 4) {
					userRepository.deleteById(users.getId());
				} else {
					users.increaseReportCount();
					sendNotifications(users);
				}
				template.convertAndSend("/sub/user/" + report.getId(),
					UserReportResultResponse.ofCommunity(communityReport, true));
			}
			case COMMENT -> {
				CommentReport commentReport = commentReportRepository.getById(reportStatusRequest.id());
				commentReport.isWait();
				commentReport.approveStatus();

				Users report = commentReport.getUsers();
				Users users = commentReport.getComment().getUsers();
				if (users.getReportCount() == 4) {
					userRepository.deleteById(users.getId());
				} else {
					users.increaseReportCount();
					sendNotifications(users);
				}
				template.convertAndSend("/sub/user/" + report.getId(),
					UserReportResultResponse.ofCommentReport(commentReport, true));
			}
		}

		return responseService.getGeneralResponse(HttpStatus.OK.value(), "신고가 승인되었습니다.");
	}
	private void sendNotifications(Users users) throws IOException {
		Long postAuthor=users.getId();
		String title = "[SUNNY]" ;
		String bodyTitle = users.getReportCount()+"번 째 경고를 받았어요";
		String body = users.getReportCount()+"번 째 경고를 받았어요";
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

	// TODO 신고 결과 API 호출 수정해야 됨
	private void reportResultNotifications(Users users) throws IOException {
		Long postAuthor=users.getId();
		String title = "[SUNNY]" ;
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
	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> refuseUserReport(ReportStatusRequest reportStatusRequest) {
		switch (reportStatusRequest.status()) {
			case COMMUNITY -> {
				CommunityReport communityReport = communityReportRepository.getById(reportStatusRequest.id());
				communityReport.isWait();
				template.convertAndSend("/sub/user/" + communityReport.getUsers().getId(),
					UserReportResultResponse.ofCommunity(communityReport, false));
				communityReportRepository.deleteById(reportStatusRequest.id());
			}
			case COMMENT -> {
				CommentReport commentReport = commentReportRepository.getById(reportStatusRequest.id());
				commentReport.isWait();
				template.convertAndSend("/sub/user/" + commentReport.getUsers().getId(),
					UserReportResultResponse.ofCommentReport(commentReport, false));
				commentReportRepository.deleteById(reportStatusRequest.id());
			}
		}
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "신고가 거절되었습니다.");
	}
}
