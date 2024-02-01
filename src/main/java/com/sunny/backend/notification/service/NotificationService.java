package com.sunny.backend.notification.service;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.response.CommonResponse.ListResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.FriendsNotification;
import com.sunny.backend.notification.dto.request.NotificationRequest;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.dto.response.AlarmListResponse;
import com.sunny.backend.notification.dto.response.NotificationResponse;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.notification.repository.CompetitionNotificationRepository;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.Users;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import static com.sunny.backend.common.CommonErrorCode.NOTIFICATIONS_NOT_SENT;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final ResponseService responseService;
    private final NotificationRepository notificationRepository;
    private final CommentNotificationRepository commentNotificationRepository;
    private final FriendsNotificationRepository friendsNotificationRepository;
    private final CompetitionNotificationRepository competitionNotificationRepository;

    private String expoPushNotificationUrl = "https://exp.host/--/api/v2/push/send";
    public ResponseEntity<CommonResponse.GeneralResponse> allowNotification(CustomUserPrincipal customUserPrincipal, NotificationRequest notificationRequest) {
        Users user = customUserPrincipal.getUsers();
        Notification notification = Notification.builder()
                .DeviceToken(notificationRequest.getTargetToken())
                .users(user)
                .build();
        notificationRepository.save(notification);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "토큰 저장 성공");
        }

    public ResponseEntity<ListResponse<AlarmListResponse>> getAlarmList(CustomUserPrincipal customUserPrincipal) {
        List<CommentNotification> commentNotifications = commentNotificationRepository.findByUsers_Id(customUserPrincipal.getUsers().getId());
        List<FriendsNotification> friendsNotifications = friendsNotificationRepository.findByFriend_Id(customUserPrincipal.getUsers().getId());
        List<CompetitionNotification> competitionNotifications = competitionNotificationRepository.findByUsers_Id(customUserPrincipal.getUsers().getId());

        List<AlarmListResponse> commentNotificationResponse = AlarmListResponse.commentNotification(commentNotifications);
        List<AlarmListResponse> friendsNotificationResponse = AlarmListResponse.friendsNotification.freindsFrom(friendsNotifications);
        List<AlarmListResponse> competitionNotificationResponse = AlarmListResponse.CompetitionNotificationResponse.competitionFrom(competitionNotifications);

        List<AlarmListResponse> combinedList = Stream.concat(
                Stream.concat(commentNotificationResponse.stream(), friendsNotificationResponse.stream()),
                competitionNotificationResponse.stream()
            )
            .toList();

        return responseService.getListResponse(HttpStatus.OK.value(), combinedList,"알림 조회 저장 성공");
    }

    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> sendNotificationToFriends(
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
                        " \"body\": \"" + notificationPushRequest.getTitle() + "\\n\\\"" + notificationPushRequest.getBody() + "\\\"\"\n"+
                        "}");

                Request request = new Request.Builder()
                    .url(expoPushNotificationUrl)
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

            NotificationResponse notificationResponse = new NotificationResponse(
                notificationPushRequest.getTitle(),
                notificationPushRequest.getBody(),
                "알림을 성공적으로 전송했습니다."
            );
            return responseService.getSingleResponse(HttpStatus.OK.value(), notificationResponse,
                "알림 성공");
        } else {
            throw new CommonCustomException(NOTIFICATIONS_NOT_SENT);
        }
    }

}