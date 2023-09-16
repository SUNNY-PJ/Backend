package com.sunny.backend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "0. User", description = "User API")
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final ResponseService responseService;

	@ApiOperation(tags = "0. User", value = "http://localhost:8080/oauth2/authorize/kakao?redirect_uri=http://localhost:8080/auth/token")
	@GetMapping("/token")
	public ResponseEntity<CommonResponse.GeneralResponse> login(){
		return ResponseEntity.ok().body(responseService.getGeneralResponse(HttpStatus.OK.value(),
			"실제로는 설명에 있는 주소를 사용합니다. \n (localhost -> 실제 주소"));
	}
}
