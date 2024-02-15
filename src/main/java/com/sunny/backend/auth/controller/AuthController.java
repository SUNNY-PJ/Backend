package com.sunny.backend.auth.controller;

import com.sunny.backend.auth.dto.KakaoRequest;
import javax.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.auth.service.AuthService;
import com.sunny.backend.auth.service.KakaoService;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "0. Auth", description = "Auth API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final ResponseService responseService;
	private final KakaoService kakaoService;
	private final AuthService authService;
	
	// Todo
	// 안쓰는거 확정시 삭제 예정
	// @ApiOperation(tags = "0. Auth", value = "카카오 로그인")
	// @GetMapping("/token")
	// public ResponseEntity<CommonResponse.SingleResponse<TokenResponse>> getKakaoAccount(
	// 	@RequestParam("accessToken") String accessToken,
	// 	@RequestParam("refreshToken") String refreshToken) {
	// 	return responseService.getSingleResponse(HttpStatus.OK.value(),
	// 		new TokenResponse(accessToken, refreshToken), "카카오 로그인 성공");
	// }

//	@ApiOperation(tags = "0. Auth", value = "카카오 로그인 callback")
//	@GetMapping("/kakao/callback")
//	public ResponseEntity<String> kakaoCallback(String code) {
////		TokenResponse tokenResponse = kakaoService.getEmailForUserInfo(code);
//
//		String accessToken = kakaoService.getAccessToken(code);
//		System.out.println(accessToken);
//		// Assuming kakaoService.getAccessToken returns the access token
////		HttpHeaders headers = new HttpHeaders();
////		headers.add("Authorization", accessToken);
////		System.out.println(headers);
////		// Return the access token as a JSON response
//		return new ResponseEntity<>(accessToken, HttpStatus.OK);
//	}

	@ApiOperation(tags = "0. Auth", value = "카카오 로그인 callback")
	@GetMapping("/kakao/callback")
	public ResponseEntity<CommonResponse.SingleResponse<TokenResponse>> kakaoCallback(String code) {
		TokenResponse tokenResponse = kakaoService.getEmailForUserInfo(code);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				tokenResponse, "카카오 로그인");
	}

	//test
	@PostMapping("/test/kakao")
	public ResponseEntity<TokenResponse> kakaoLogin(@RequestBody KakaoRequest kakaoRequest) {
		System.out.println("호출");
		TokenResponse tokenResponse = kakaoService.kakaoLogin(kakaoRequest);
		return ResponseEntity.ok(tokenResponse);
	}

	@ApiOperation(tags = "0. Auth", value = "닉네임 변경")
	@PostMapping("/nickname")
	public ResponseEntity<CommonResponse.SingleResponse<UserNameResponse>> changeNickname(
		@AuthUser CustomUserPrincipal customUserPrincipal, 
		@RequestParam("name") @Size(min = 2, max = 10, message = "2~10자 이내로 입력해야 합니다.") String name) {
		UserNameResponse userDto = kakaoService.changeNickname(customUserPrincipal, name);
		return responseService.getSingleResponse(HttpStatus.OK.value(), userDto, "닉네임 변경 성공");
	}

	//이거 일단 임시 테스트임
	@ApiOperation(tags = "0. Auth", value = "로그아웃")
	@GetMapping("/kakao/logout")
	public ResponseEntity<Void> handleKakaoLogout(@AuthUser CustomUserPrincipal customUserPrincipal) {
		kakaoService.logout(customUserPrincipal);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(tags = "0. Auth", value = "탈퇴")
	@GetMapping("/leave")
	public ResponseEntity<Void> deleteAccount(@AuthUser CustomUserPrincipal customUserPrincipal) {
		kakaoService.leave(customUserPrincipal);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(tags = "0. Auth", value = "refresh 토큰으로 access 토큰 발급")
	@GetMapping("/reissue")
	public ResponseEntity<TokenResponse> reissue(@RequestParam(name = "refreshToken") String refreshToken) {
		TokenResponse tokenResponse = authService.reissue(refreshToken);
		return ResponseEntity.ok().body(tokenResponse);
	}
}