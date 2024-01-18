package com.sunny.backend.notification.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.NotificationRequestDto;
import com.sunny.backend.dto.request.PushRequestDto;
import com.sunny.backend.dto.response.NotificationResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.notification.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "9. Alarm", description = "Alarm API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class NotificationController {
    private final NotificationService notificationService;
    @ApiOperation(tags = "9. Alarm", value = "알림 토큰 전송")
    @PostMapping("/token")
    public ResponseEntity<CommonResponse.GeneralResponse> allowNotification(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @Valid @RequestBody NotificationRequestDto notificationRequestDto) {
        return notificationService.allowNotification(customUserPrincipal,notificationRequestDto);
    }

    @ApiOperation(tags = "9. Alarm", value = "알림 전송")
    @PostMapping("")
    public ResponseEntity<CommonResponse.SingleResponse<NotificationResponse>> sendNotificationToFriends(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @Valid @RequestBody PushRequestDto pushRequestDto) throws IOException {
        return notificationService.sendNotificationToFriends(customUserPrincipal,pushRequestDto);
    }
}
