package com.sunny.backend.notification.controller;

import com.sunny.backend.common.response.CommonResponse.SingleResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse.ListResponse;
import com.sunny.backend.notification.dto.request.NotificationRequest;
import com.sunny.backend.notification.dto.response.AlarmListResponse;
import com.sunny.backend.notification.service.NotificationService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "9. Alarm", description = "Alarm API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class NotificationController {
	private final NotificationService notificationService;


	@ApiOperation(tags = "9. Alarm", value = "알림 리스트 확인")
	@GetMapping("/permission/allow")
	public ResponseEntity<SingleResponse<Boolean>> getPermissionAlarm(
			@AuthUser CustomUserPrincipal customUserPrincipal) {
		return notificationService.getPermissionAlarm(customUserPrincipal);
	}

	@ApiOperation(tags = "9. Alarm", value = "알림 리스트 확인")
	@GetMapping("")
	public ResponseEntity<ListResponse<AlarmListResponse>> getAlarmList(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		return notificationService.getAlarmList(customUserPrincipal);
	}

	@ApiOperation(tags = "9. Alarm", value = "알림 허용/거절")
	@PostMapping("/permission")
	public ResponseEntity<SingleResponse<Boolean>> permissionAlarm(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@RequestBody NotificationRequest.NotificationAllowRequest notificationAllowRequest) {
		return notificationService.permissionAlarm(customUserPrincipal,notificationAllowRequest);
	}

}
