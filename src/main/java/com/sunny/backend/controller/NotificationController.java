package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.NotificationRequestDto;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
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
    @PostMapping("")
    public ResponseEntity<CommonResponse.GeneralResponse> pushNotification(
            @AuthUser CustomUserPrincipal customUserPrincipal,
            @RequestBody NotificationRequestDto notificationRequestDto) throws IOException {
        return notificationService.allowNotification(customUserPrincipal,notificationRequestDto);
    }
}
