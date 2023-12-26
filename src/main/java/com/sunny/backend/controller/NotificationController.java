package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.dto.request.FcmRequestDto;
import com.sunny.backend.dto.response.NotificationResponse;
import com.sunny.backend.dto.response.ProfileResponse;
import com.sunny.backend.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class NotificationController {
    private final NotificationService notificationService;
    @ApiOperation(tags = "9. Alarm", value = "알림 설정")
    @GetMapping("")
    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> pushNotification(@RequestBody FcmRequestDto fcmRequestDto) throws IOException {
        return notificationService.sendPushNotification(fcmRequestDto);
    }
}
