package com.sunny.backend.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.dto.request.FcmRequestDto;
import com.sunny.backend.dto.response.NotificationResponse;
import com.sunny.backend.service.NotificationService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "9. Alarm", description = "Alarm API")
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class NotificationController {
    private final NotificationService notificationService;
    @ApiOperation(tags = "9. Alarm", value = "알림 설정")
    @PostMapping("")
    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> pushNotification(@RequestBody FcmRequestDto fcmRequestDto) throws IOException {
        return notificationService.sendPushNotification(fcmRequestDto);
    }


}
