package com.sunny.backend.user.controller;

import com.sunny.backend.service.KaKaoService;
import com.sunny.backend.user.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Tag(name = "0. User", description = "User API")
@CrossOrigin(origins = "http://localhost:19006")
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final ResponseService responseService;
	private final KaKaoService kaKaoService;
	private final UsersService usersService;


	@ApiOperation(tags = "0. User", value = "http://localhost:8080/oauth2/authorize/kakao?redirect_uri=http://localhost:8080/auth/token")
	@GetMapping("/token")
	public ResponseEntity<CommonResponse.GeneralResponse> login(){
		return ResponseEntity.ok().body(responseService.getGeneralResponse(HttpStatus.OK.value(),
			"실제로는 설명에 있는 주소를 사용합니다. \n (localhost -> 실제 주소"));
	}

	@GetMapping("/auth/kakao/callback")
	public ResponseEntity<CommonResponse.GeneralResponse>  kakaoCallback(String code) throws IOException { // Data를 리턴해주는 컨트롤러 함수

		String access_Token = kaKaoService.getAccessToken(code);
		System.out.println(access_Token);
		return ResponseEntity.ok()
				.body(usersService.kakaoLogin(access_Token));
	}

	}

