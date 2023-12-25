package com.sunny.backend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.security.dto.AuthDto;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.KaKaoService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "0. User", description = "User API")
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final ResponseService responseService;
	private final KaKaoService kaKaoService;

	@ApiOperation(tags = "0. User", value = "카카오 로그인")
	@GetMapping("/auth/token")
	public ResponseEntity<CommonResponse.SingleResponse<AuthDto.TokenDto>> getKakaoAccount(
		@RequestParam("accessToken") String accessToken, @RequestParam("refreshToken") String refreshToken) {
		return responseService.getSingleResponse(HttpStatus.OK.value(),
			new AuthDto.TokenDto(accessToken, refreshToken), "카카오 로그인 성공");

	}

	@ApiOperation(tags = "0. User", value = "카카오 로그인 callback")
	@GetMapping("/auth/kakao/callback")
	public ResponseEntity<CommonResponse.SingleResponse<AuthDto.TokenDto>> kakaoCallback(String code) throws
		Exception { // Data를 리턴해주는 컨트롤러 함수
		AuthDto.TokenDto tokenDto = kaKaoService.getAccessToken(code);

		return responseService.getSingleResponse(HttpStatus.OK.value(), tokenDto, "카카오 로그인 성공");
	}

	@ApiOperation(tags = "0. User", value = "닉네임 변경")
	@PostMapping("/auth/nickname")
	public ResponseEntity<CommonResponse.SingleResponse<AuthDto.UserDto>> changeNickname(
		@AuthUser CustomUserPrincipal customUserPrincipal, @RequestParam("name") String name) {
		AuthDto.UserDto userDto = kaKaoService.changeNickname(customUserPrincipal, name);

		return responseService.getSingleResponse(HttpStatus.OK.value(), userDto, "닉네임 변경 성공");
	}
}