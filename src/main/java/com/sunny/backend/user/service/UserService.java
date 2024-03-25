package com.sunny.backend.user.service;

import static com.sunny.backend.common.ComnConstant.*;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import com.sunny.backend.scrap.repository.ScrapRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.dto.response.ProfileResponse;
import com.sunny.backend.user.dto.response.UserCommentResponse;
import com.sunny.backend.user.dto.response.UserCommunityResponse;
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
		Long userTokenId = customUserPrincipal.getUsers().getId();
		Users user = checkUserId(customUserPrincipal, userId);
		return ProfileResponse.of(user, user.isOwner(userTokenId));
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
			.map(comment -> UserCommentResponse.from(comment, customUserPrincipal.getUsers()))
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

	// TODO 신고 결과 API 호출 수정해야 됨
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
