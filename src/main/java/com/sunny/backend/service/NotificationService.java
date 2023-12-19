package com.sunny.backend.service;

import com.sunny.backend.dto.request.FcmRequestDto;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private  String expoPushNotificationUrl = "https://exp.host/--/api/v2/push/send";
    public ResponseEntity<String> sendPushNotification(FcmRequestDto fcmRequestDto) {
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

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println("Push notification sent. Response: " + responseBody);
            return ResponseEntity.ok(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error sending push notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending push notification");
        }
    }
}