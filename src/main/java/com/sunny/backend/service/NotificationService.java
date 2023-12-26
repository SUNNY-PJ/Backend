package com.sunny.backend.service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.FcmRequestDto;
import com.sunny.backend.dto.response.NotificationResponse;
import com.sunny.backend.dto.response.comment.CommentResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final ResponseService responseService;

    private String expoPushNotificationUrl = "https://exp.host/--/api/v2/push/send";
    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> sendPushNotification(FcmRequestDto fcmRequestDto) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType,
                "{\n" +
                        "  \"to\": \"" + fcmRequestDto.getTargetToken() + "\",\n" +
                        "  \"title\": \"" + fcmRequestDto.getTitle() + "\",\n" +
                        "  \"body\": \"" + fcmRequestDto.getBody() + "\"\n" +
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
                    fcmRequestDto.getTitle(),
                    fcmRequestDto.getBody(),
                    responseBody
            );

            return responseService.getSingleResponse(HttpStatus.OK.value(), notificationResponse, "알림 성공");
        }

}