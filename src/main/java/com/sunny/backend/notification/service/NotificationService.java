package com.sunny.backend.notification.service;

import static com.sunny.backend.common.ComnConstant.*;
import static com.sunny.backend.friends.domain.FriendStatus.*;
import static com.sunny.backend.notification.dto.response.AlarmListResponse.*;
import static com.sunny.backend.notification.exception.NotificationErrorCode.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.CommonResponse.ListResponse;
import com.sunny.backend.common.response.CommonResponse.SingleResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.domain.UserReportNotification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.dto.request.NotificationRequest.NotificationAllowRequest;
import com.sunny.backend.notification.dto.response.AlarmListResponse;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.notification.repository.CompetitionNotificationRepository;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.repository.UserReportNotificationRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final ResponseService responseService;
	private final NotificationRepository notificationRepository;
	private final CommentNotificationRepository commentNotificationRepository;
	private final FriendsNotificationRepository friendsNotificationRepository;
	private final UserRepository userRepository;
	private final CompetitionNotificationRepository competitionNotificationRepository;
	private final FriendRepository friendRepository;
	private final UserReportNotificationRepository userReportNotificationRepository;

	@Transactional
	public ResponseEntity<SingleResponse<Boolean>> permissionAlarm(CustomUserPrincipal customUserPrincipal,
		NotificationAllowRequest notificationAllowRequest) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		if (notificationAllowRequest.isAllow()) {
			if (notificationAllowRequest.getTargetToken() == null || notificationAllowRequest.getTargetToken()
				.isEmpty()) {
				return responseService.getSingleResponse(HttpStatus.BAD_REQUEST.value(), null, "디바이스 토큰 값은 필수 값입니다.");
			}
			log.info("User DeviceToken= " + notificationAllowRequest.getTargetToken());
			Notification notification = Notification.builder()
				.deviceToken(notificationAllowRequest.getTargetToken())
				.users(user)
				.build();
			notificationRepository.save(notification);
		} else {
			notificationRepository.deleteByUsers(user);
		}
		return responseService.getSingleResponse(HttpStatus.OK.value(), notificationAllowRequest.isAllow(),
			"알림 허용/거부 성공");
	}

	public ResponseEntity<ListResponse<AlarmListResponse>> getAlarmList(CustomUserPrincipal customUserPrincipal) {

		List<CommentNotification> commentNotifications = commentNotificationRepository.findByUsers_Id(
			customUserPrincipal.getId());
		List<AlarmListResponse> commentNotificationResponse = fromCommentNotifications(commentNotifications,
			customUserPrincipal.getId());
		List<FriendsNotification> friendsNotifications = friendsNotificationRepository.findByUsers_Id(
			customUserPrincipal.getId());
		Users users = userRepository.getById(customUserPrincipal.getId());
		List<Friend> friends = friendRepository.findByUsers(users);

		List<AlarmListResponse> friendsNotificationResponse = friendsNotifications.stream()
			.map(friendsNotification -> {
				Friend friend = friends.stream()
					.filter(f -> f.getId().equals(friendsNotification.getFriendId()))
					.findFirst()
					.orElse(null);
				boolean isFriend = friend != null && friend.getStatus() == FRIEND;
				return AlarmListResponse.fromFriendsAlert(friendsNotification, isFriend);
			})
			.toList();

		// List<AlarmListResponse> friendsNotificationResponse = friendsNotifications.stream()
		// 	.map(AlarmListResponse::fromFriendsAlert)
		// 	.toList();

		List<CompetitionNotification> competitionNotifications = competitionNotificationRepository.findByUsers_Id(
			customUserPrincipal.getId());
		List<UserReportNotification> userReportNotifications = userReportNotificationRepository.findByUsers_Id(
			customUserPrincipal.getId());
		List<AlarmListResponse> competitionNotificationResponse = competitionNotifications.stream()
			.map(AlarmListResponse::fromCompetitionAlert)
			.toList();
		List<AlarmListResponse> userReportNotificationResponse = userReportNotifications.stream()
			.map(AlarmListResponse::fromUserReportAlert)
			.toList();

		List<AlarmListResponse> combinedList = Stream.of(commentNotificationResponse.stream(),
				friendsNotificationResponse.stream(),
				competitionNotificationResponse.stream(),
				userReportNotificationResponse.stream())
			.flatMap(Function.identity())
			.sorted(Comparator.comparing(AlarmListResponse::createdAt).reversed())
			.toList();
		return responseService.getListResponse(HttpStatus.OK.value(), combinedList, "알림 조회 저장 성공");
	}

	public void sendNotificationToFriends(
		String title, NotificationPushRequest notificationPushRequest) {
		List<Notification> notifications = notificationRepository.findByUsers_Id(
			notificationPushRequest.getPostAuthor());
		if (notifications != null && !notifications.isEmpty()) {
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			for (Notification notification : notifications) {
				RequestBody body = RequestBody.create(mediaType,
					"{\n" +
						" \"to\": \"" + notification.getDeviceToken() + "\",\n" +
						" \"title\": \"" + title + "\",\n" +
						" \"body\": \"" + notificationPushRequest.getTitle() + "\"\n" +
						"}");

				Request request = new Request.Builder()
					.url(EXPO_PUSH_URL)
					.post(body)
					.addHeader("Content-Type", "application/json")
					.addHeader("Accept", "application/json")
					.build();
				try {
					Response response = client.newCall(request).execute();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		} else {
			throw new CustomException(NOTIFICATIONS_NOT_SENT);
		}
	}

	public ResponseEntity<CommonResponse.SingleResponse<Boolean>> getPermissionAlarm(
		CustomUserPrincipal customUserPrincipal) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		List<Notification> notificationList = notificationRepository.findByUsers_Id(user.getId());
		if (notificationList.size() != 0) {
			return responseService.getSingleResponse(HttpStatus.OK.value(), true, "알림 허용");
		} else {
			return responseService.getSingleResponse(HttpStatus.OK.value(), false, "알림 거부");
		}
	}

	public void commentSendNotificationToFriends(
		String title, NotificationPushRequest notificationPushRequest) {
		List<Notification> notifications = notificationRepository.findByUsers_Id(
			notificationPushRequest.getPostAuthor());
		if (notifications != null && !notifications.isEmpty()) {
			OkHttpClient client = new OkHttpClient();

			MediaType mediaType = MediaType.parse("application/json");

			for (Notification notification : notifications) {
				RequestBody body = RequestBody.create(mediaType,
					"{\n" +
						"  \"to\": \"" + notification.getDeviceToken() + "\",\n" +
						"  \"title\": \"" + title + "\",\n" +
						" \"body\": \"" + notificationPushRequest.getTitle() + "\\n\\\""
						+ notificationPushRequest.getBody() + "\\\"\"\n" +
						"}");

				Request request = new Request.Builder()
					.url(EXPO_PUSH_URL)
					.post(body)
					.addHeader("Content-Type", "application/json")
					.addHeader("Accept", "application/json")
					.build();
				try {
					Response response = client.newCall(request).execute();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		} else {
			throw new CustomException(NOTIFICATIONS_NOT_SENT);
		}
	}
}