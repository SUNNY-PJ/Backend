package com.sunny.backend.user.controller;

import com.sunny.backend.service.KaKaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.security.dto.AuthDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Tag(name = "0. User", description = "User API")
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final ResponseService responseService;
	private final KaKaoService kaKaoService;


	@GetMapping("/auth/token")
	public ResponseEntity<CommonResponse.SingleResponse<AuthDto.TokenDto>> getKakaoAccount(
		@RequestParam("accessToken") String accessToken, @RequestParam("refreshToken") String refreshToken) {

		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), 
			new AuthDto.TokenDto(accessToken, refreshToken), "카카오 로그인 성공"));

	}

	@GetMapping("/auth/kakao/callback")
	public ResponseEntity<CommonResponse.SingleResponse<AuthDto.TokenDto>> kakaoCallback(String code) throws Exception { // Data를 리턴해주는 컨트롤러 함수
		AuthDto.TokenDto tokenDto = kaKaoService.getAccessToken(code);

		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), tokenDto, "카카오 로그인 성공"));
	}
}

