package com.sunny.backend.auth;
import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;

import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		TokenResponse tokenResponse = appleOAuthClient.getOAuthMemberId(idToken);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				tokenResponse, "애플 로그인");
	}

	@ApiOperation(tags = "0. Auth", value = "닉네임 변경")
	@PostMapping("/nickname")
	public ResponseEntity<CommonResponse.SingleResponse<UserNameResponse>> changeNickname(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@RequestParam("name") @Size(min = 2, max = 10, message = "2~10자 이내로 입력해야 합니다.") String name) {
		UserNameResponse userDto = appleService.changeNickname(customUserPrincipal, name);
		return responseService.getSingleResponse(HttpStatus.OK.value(), userDto, "닉네임 변경 성공");
	}


	@ApiOperation(tags = "0. Auth", value = "탈퇴")
	@GetMapping("/leave")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteAccount(
			@AuthUser CustomUserPrincipal customUserPrincipal,
			@RequestParam("code") String code) {
		return appleService.revoke(customUserPrincipal, code);
	}
}

