package com.sunny.backend.notification.service;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.notification.domain.NotificationType;
import com.sunny.backend.notification.dto.request.NotificationRequest;
import com.sunny.backend.notification.dto.request.NotificationPushReques;
import com.sunny.backend.notification.dto.response.AlarmResponse;
import com.sunny.backend.notification.dto.response.NotificationResponse;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.RedisUtil;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import static com.sunny.backend.common.CommonErrorCode.NOTIFICATIONS_NOT_SENT;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RedisUtil redisUtil;
    private final ResponseService responseService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

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


//    //이건 혹시 모르는 사용자 자체 알림?
//
//    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> sendPushNotification(CustomUserPrincipal customUserPrincipal, PushRequestDto pushRequestDto) throws IOException {
//
//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json");
//
//        RequestBody body = RequestBody.create(mediaType,
//                "{\n" +
//                        "  \"to\": \"" + pushRequestDto.getTargetToken() + "\",\n" +
//                        "  \"title\": \"" + pushRequestDto.getTitle() + "\",\n" +
//                        "  \"body\": \"" + pushRequestDto.getBody() + "\"\n" +
//                        "}");
//
//        Request request = new Request.Builder()
//                .url(expoPushNotificationUrl)
//                .post(body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Accept", "application/json")
//                .build();
//
//        Response response = client.newCall(request).execute();
//        String responseBody = response.body().string();
//        System.out.println("Push notification sent. Response: " + responseBody);
//
//        // Create a custom response object
//        NotificationResponse notificationResponse = new NotificationResponse(
//                pushRequestDto.getTitle(),
//                pushRequestDto.getBody(),
//                responseBody
//        );
//
//        return responseService.getSingleResponse(HttpStatus.OK.value(), notificationResponse, "알림 성공");
//    }

    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> sendNotificationToFriends(CustomUserPrincipal customUserPrincipal, NotificationPushReques notificationPushReques) throws IOException {

        List<Notification> notifications = notificationRepository.findByUsers_Id(
            notificationPushReques.getFriendsId());
        if (notifications != null && !notifications.isEmpty()) {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");

            for (Notification notification : notifications) {
                RequestBody body = RequestBody.create(mediaType,
                    "{\n" +
                        "  \"to\": \"" + notification.getDeviceToken() + "\",\n" +
                        "  \"title\": \"" + notificationPushReques.getTitle() + "\",\n" +
                        "  \"body\": \"" + notificationPushReques.getBody() + "\"\n" +
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
                notificationPushReques.getTitle(),
                notificationPushReques.getBody(),
                "알림을 성공적으로 전송했습니다."
            );
            return responseService.getSingleResponse(HttpStatus.OK.value(), notificationResponse,
                "알림 성공");
        } else {
            throw new CommonCustomException(NOTIFICATIONS_NOT_SENT);
        }
    }

    public void saveRedis(@Valid NotificationPushReques notificationPushReques) {
        Users users = userRepository.getById(notificationPushReques.getFriendsId());
        AlarmResponse alarmResponse = AlarmResponse.builder()
            .title(notificationPushReques.getTitle())
            .name(users.getName())
            .content(notificationPushReques.getBody())
            .date(LocalDateTime.now())
            .type(NotificationType.COMMENT)
            .build();
        redisUtil.setHashData(String.valueOf(notificationPushReques.getFriendsId()), alarmResponse);
        redisUtil.getHashData(String.valueOf(notificationPushReques.getFriendsId()));
    }

}