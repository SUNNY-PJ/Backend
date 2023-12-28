package com.sunny.backend.service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.CustomException;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.NotificationRequestDto;
import com.sunny.backend.dto.request.PushRequestDto;
import com.sunny.backend.dto.response.NotificationResponse;
import com.sunny.backend.entity.Notification;
import com.sunny.backend.repository.NotificationRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.sunny.backend.common.ErrorCode.NOTIFICATIONS_NOT_SENT;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final ResponseService responseService;
    private final NotificationRepository notificationRepository;

    private String expoPushNotificationUrl = "https://exp.host/--/api/v2/push/send";
    public ResponseEntity<CommonResponse.GeneralResponse> allowNotification(CustomUserPrincipal customUserPrincipal, NotificationRequestDto notificationRequestDto)  {
        Users user = customUserPrincipal.getUsers();

        Notification notification=Notification.builder()
                .DeviceToken(notificationRequestDto.getTargetToken())
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

    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> sendNotificationToFriends(CustomUserPrincipal customUserPrincipal, PushRequestDto pushRequestDto) throws IOException {

        Notification notification = notificationRepository.findByUsers_Id(pushRequestDto.getFriendsId());
        System.out.println(notification.getDeviceToken());
        if (notification != null) {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");

            RequestBody body = RequestBody.create(mediaType,
                    "{\n" +
                            "  \"to\": \"" + notification.getDeviceToken() + "\",\n" +
                            "  \"title\": \"" + pushRequestDto.getTitle() + "\",\n" +
                            "  \"body\": \"" + pushRequestDto.getBody() + "\"\n" +
                            "}");

            Request request = new Request.Builder()
                    .url(expoPushNotificationUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println("Push notification sent. Response: " + responseBody);

            // Create a custom response object
            NotificationResponse notificationResponse = new NotificationResponse(
                    pushRequestDto.getTitle(),
                    pushRequestDto.getBody(),
                    responseBody
            );
            return responseService.getSingleResponse(HttpStatus.OK.value(), notificationResponse, "알림 성공");
        }
        else {
            throw new CustomException(NOTIFICATIONS_NOT_SENT);
        }
    }
}