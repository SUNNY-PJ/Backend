package com.sunny.backend.user.service;

import static com.sunny.backend.common.ComnConstant.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
		return ProfileResponse.from(user);
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
	public ResponseEntity<CommonResponse.GeneralResponse> approveUserReport(ReportStatusRequest reportStatusRequest) {
		switch (reportStatusRequest.status()) {
			case COMMUNITY -> {
				CommunityReport communityReport = communityReportRepository.getById(reportStatusRequest.id());
				communityReport.isWait();
				communityReport.approveStatus();

				Users users = communityReport.getUsers();
				if (users.getReportCount() == 4) {
					userRepository.deleteById(users.getId());
				} else {
					communityReport.getUsers().increaseReportCount();
				}
			}
			case COMMENT -> {
				CommentReport commentReport = commentReportRepository.getById(reportStatusRequest.id());
				commentReport.isWait();
				commentReport.approveStatus();

				Users users = commentReport.getUsers();
				if (users.getReportCount() == 4) {
					userRepository.deleteById(users.getId());
				} else {
					commentReport.getUsers().increaseReportCount();
				}
			}
		}
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "신고가 승인되었습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> refuseUserReport(ReportStatusRequest reportStatusRequest) {
		switch (reportStatusRequest.status()) {
			case COMMUNITY -> {
				CommunityReport communityReport = communityReportRepository.getById(reportStatusRequest.id());
				communityReport.isWait();
				communityReportRepository.deleteById(reportStatusRequest.id());
			}
			case COMMENT -> {
				CommentReport commentReport = commentReportRepository.getById(reportStatusRequest.id());
				commentReport.isWait();
				commentReportRepository.deleteById(reportStatusRequest.id());
			}
		}
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "신고가 거절되었습니다.");
	}
}
