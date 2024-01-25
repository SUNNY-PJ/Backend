package com.sunny.backend.notification.service;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.response.CommonResponse.ListResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.dto.request.NotificationRequest;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.dto.response.CommentNotificationResponse;
import com.sunny.backend.notification.dto.response.NotificationResponse;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.Users;
import java.util.List;
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

    private String expoPushNotificationUrl = "https://exp.host/--/api/v2/push/send";
    public ResponseEntity<CommonResponse.GeneralResponse> allowNotification(CustomUserPrincipal customUserPrincipal, NotificationRequest notificationRequest) {
        Users user = customUserPrincipal.getUsers();
        System.out.println(notificationRequest.getTargetToken());
        Notification notification = Notification.builder()
                .DeviceToken(notificationRequest.getTargetToken())
                .users(user)
                .build();
        notificationRepository.save(notification);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "토큰 저장 성공");
        }

    public ResponseEntity<ListResponse<CommentNotificationResponse>> getAlarmList(CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        List<CommentNotification> users =commentNotificationRepository.findByUsers_Id(customUserPrincipal.getUsers().getId());
        List<CommentNotificationResponse> commentNotificationResponse = CommentNotificationResponse.listOf(
            users);
        return responseService.getListResponse(HttpStatus.OK.value(), commentNotificationResponse,"알림 조회 저장 성공");
    }

    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> sendNotificationToFriends(
        CustomUserPrincipal customUserPrincipal,
        NotificationPushRequest notificationPushRequest) throws IOException {
        List<Notification> notifications = notificationRepository.findByUsers_Id(
            notificationPushRequest.getPostAuthor());
        if (notifications != null && !notifications.isEmpty()) {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");

            for (Notification notification : notifications) {
                String comment="새로운 댓글이 달렸어요";

                RequestBody body = RequestBody.create(mediaType,
                    "{\n" +
                        "  \"to\": \"" + notification.getDeviceToken() + "\",\n" +
                        "  \"title\": \"" + notificationPushRequest.getTitle() + "\",\n" +
                        " \"body\": \"" + comment + "\\n\\\"" + notificationPushRequest.getBody() + "\\\"\"\n"+
                        "}");

                Request request = new Request.Builder()
                    .url(expoPushNotificationUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
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