package com.sunny.backend.apple.controller;

import javax.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.apple.service.AppleOAuthClient;
import com.sunny.backend.apple.service.AppleService;
import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.dto.UserRequest;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/apple/auth")
@RequiredArgsConstructor
@Slf4j
public class AppleController {

	private final AppleService appleService;
	private final AppleOAuthClient appleOAuthClient;
	private final ResponseService responseService;

	@ApiOperation(tags = "0. Auth", value = "애플 로그인 callback")
	@GetMapping("/callback")
	public ResponseEntity<CommonResponse.SingleResponse<TokenResponse>> verifyToken(
		@RequestParam("code") String idToken) {
		log.info(idToken);
		TokenResponse tokenResponse = appleOAuthClient.getOAuthMemberId(idToken);
		log.info(String.valueOf(tokenResponse));
		return responseService.getSingleResponse(HttpStatus.OK.value(),
			tokenResponse, "애플 로그인 성공");
	}

	@ApiOperation(tags = "0. Auth", value = "닉네임 변경")
	@PostMapping("/nickname")
	public ResponseEntity<CommonResponse.SingleResponse<UserNameResponse>> changeNickname(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam("name") @Size(min = 2, max = 10, message = "2~10자 이내로 입력해야 합니다.") String name) {
		UserNameResponse userDto = appleService.changeNickname(customUserPrincipal, name);
		return responseService.getSingleResponse(HttpStatus.OK.value(), userDto, "닉네임 변경 성공");
	}

	@ApiOperation(tags = "0. Auth", value = "애플 탈퇴")
	@GetMapping("/leave")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteAccount(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam("code") String code) {
		ResponseEntity<CommonResponse.GeneralResponse> response = appleService.revokeToken(customUserPrincipal.getId(),
			code);
		log.info("revokeToken: {}", response);
		return response;
	}

	@ApiOperation(tags = "0. Auth", value = "refresh 토큰으로 access 토큰 발급")
	@GetMapping("/reissue")
	public ResponseEntity<?> reissue(@RequestParam(name = "refreshToken") String refreshToken) {
		return ResponseEntity.ok().body(appleService.reissue(refreshToken));
	}

	@ApiOperation(tags = "0. Auth", value = "애플 로그아웃")
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@Validated @RequestBody UserRequest logout) {
		return appleService.logout(logout);
	}
}
