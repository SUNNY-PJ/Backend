package com.sunny.backend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.security.dto.AuthDto;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "0. User", description = "User API")
@CrossOrigin(origins = "http://localhost:19006")
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final ResponseService responseService;

	@ApiOperation(tags = "0. User", value = "http://localhost:8080/oauth2/authorize/kakao?redirect_uri=http://localhost:8080/auth/token - "
		+ "실제로는 설명에 있는 주소를 사용합니다. \n (localhost -> 실제 주소)")
	@ApiResponses({
		@ApiResponse(code = 200, message = "실제로는 설명에 있는 주소를 사용합니다. \n (localhost -> 실제 주소)"),
	})
	@GetMapping("/token")
	public String login(){
		return "실제로는 설명에 있는 주소를 사용합니다. \n (localhost -> 실제 주소";
	}

	@GetMapping("/auth/token")
	public ResponseEntity<CommonResponse.SingleResponse<AuthDto.TokenDto>> getKakaoAccount(
		@RequestParam("accessToken") String accessToken, @RequestParam("refreshToken") String refreshToken) {

		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), 
			new AuthDto.TokenDto(accessToken, refreshToken), "카카오 로그인 성공"));

	}
}
