package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.FcmRequestDto;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class TokenController {
    private final NotificationService notificationService;
    @ApiOperation(tags = "9. Alarm", value = "알림 설정")
    @GetMapping("")
    public ResponseEntity getScrapsByUserId(@RequestBody FcmRequestDto fcmRequestDto){
        return ResponseEntity.ok().body(notificationService.sendPushNotification(fcmRequestDto));
    }
}
