package com.sunny.backend.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.auth.service.KaKaoService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;

@Tag(name = "0. Auth", description = "Auth API")
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final ResponseService responseService;
	private final KaKaoService kaKaoService;

	@ApiOperation(tags = "0. User", value = "카카오 로그인")
	@GetMapping("/auth/token")
	public ResponseEntity<CommonResponse.SingleResponse<TokenResponse>> getKakaoAccount(
			@RequestParam("accessToken") String accessToken,
			@RequestParam("refreshToken") String refreshToken) {
		return responseService.getSingleResponse(HttpStatus.OK.value(),
			new TokenResponse(accessToken, refreshToken), "카카오 로그인 성공");

	}

	@ApiOperation(tags = "0. User", value = "카카오 로그인 callback")
	@GetMapping("/auth/kakao/callback")
	public ResponseEntity<CommonResponse.SingleResponse<TokenResponse>> kakaoCallback(String code) throws Exception { // Data를 리턴해주는 컨트롤러 함수
		TokenResponse tokenResponse = kaKaoService.getAccessToken(code);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", tokenResponse.accessToken());
		return responseService.getSingleResponse(HttpStatus.OK.value(),
			tokenResponse, "카카오 로그인");
	}

	@ApiOperation(tags = "0. User", value = "닉네임 변경")
	@PostMapping("/auth/nickname")
	public ResponseEntity<CommonResponse.SingleResponse<UserNameResponse>> changeNickname(
			@AuthUser CustomUserPrincipal customUserPrincipal, @RequestParam("name")
	@Size(min = 2, max = 10, message = "2~10자 이내로 입력해야 합니다.") String name) {
		UserNameResponse userDto = kaKaoService.changeNickname(customUserPrincipal, name);

		return responseService.getSingleResponse(HttpStatus.OK.value(), userDto, "닉네임 변경 성공");
	}
}