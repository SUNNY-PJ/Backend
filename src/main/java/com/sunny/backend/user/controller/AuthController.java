package com.sunny.backend.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "0. User", description = "User API")
@RestController
@RequiredArgsConstructor
public class AuthController {

	@ApiOperation(tags = "0. User", value = "http://localhost:8080/oauth2/authorize/kakao?redirect_uri=http://localhost:8080/auth/token - "
		+ "실제로는 설명에 있는 주소를 사용합니다. \n (localhost -> 실제 주소)")
	@ApiResponses({
		@ApiResponse(code = 200, message = "실제로는 설명에 있는 주소를 사용합니다. \n (localhost -> 실제 주소)"),
	})
	@GetMapping("/token")
	public String login(){
		return "실제로는 설명에 있는 주소를 사용합니다. \n (localhost -> 실제 주소";
	}
}
