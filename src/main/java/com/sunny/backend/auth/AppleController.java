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
@RequestMapping("/apple")
@RequiredArgsConstructor
@Slf4j
public class AppleController {

  private final AppleService appleService;
  private final AppleOAuthClient appleOAuthClient;
  private final ResponseService responseService;

  @ApiOperation(tags = "0. Auth", value = "애플 로그인 test")
  @GetMapping("/auth/callback")
  public void appleCallback(@RequestParam("code") String code) throws IOException {
		log.info("code={}", code);
    log.info("apple callback method 호출");
    appleService.getIdToken(code);
  }

  @ApiOperation(tags = "0. Auth", value = "애플 로그인 callback")
  @GetMapping("/auth")
  public ResponseEntity<CommonResponse.SingleResponse<TokenResponse>>  verifyToken(@RequestParam("code") String idToken) {
    log.info("호출");
    TokenResponse tokenResponse= appleOAuthClient.getOAuthMemberId(idToken);
    System.out.println("tokenResponse= "+tokenResponse.accessToken());
    return responseService.getSingleResponse(HttpStatus.OK.value(),
        tokenResponse, "애플 로그인");
  }

//  @ApiOperation(tags = "0. Auth", value = "애플 로그인 id token & jwt test")
//  @GetMapping("/auth")
//  public ResponseEntity<CommonResponse.SingleResponse<TokenResponse>>  verifyToken(
//      @RequestParam("token") String idToken,
//      @RequestParam("code") String code
//      ) {
//    log.info("호출");
//    TokenResponse tokenResponse= appleOAuthClient.getOAuthMemberIdTest(idToken,code);
//    System.out.println("tokenResponse= "+tokenResponse.accessToken());
//    return responseService.getSingleResponse(HttpStatus.OK.value(),
//        tokenResponse, "애플 로그인");
//  }

  @ApiOperation(tags = "0. Auth", value = "닉네임 변경")
	@PostMapping("/nickname")
	public ResponseEntity<CommonResponse.SingleResponse<UserNameResponse>> changeNickname(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@RequestParam("name") @Size(min = 2, max = 10, message = "2~10자 이내로 입력해야 합니다.") String name) {
		UserNameResponse userDto = appleOAuthClient.changeNickname(customUserPrincipal, name);
		return responseService.getSingleResponse(HttpStatus.OK.value(), userDto, "닉네임 변경 성공");
	}

	//이거 일단 임시 테스트임
//	@ApiOperation(tags = "0. Auth", value = "로그아웃")
//	@GetMapping("/kakao/logout")
//	public ResponseEntity<Void> handleKakaoLogout(@AuthUser CustomUserPrincipal customUserPrincipal) {
//		appleOAuthClient.logout(customUserPrincipal);
//		return ResponseEntity.ok().build();
//	}

	@ApiOperation(tags = "0. Auth", value = "탈퇴")
	@GetMapping("/leave")
	public ResponseEntity<Void> deleteAccount(@AuthUser CustomUserPrincipal customUserPrincipal) {
		appleOAuthClient.leave(customUserPrincipal);
		return ResponseEntity.ok().build();
	}

//
//	@ApiOperation(tags = "0. Auth", value = "refresh 토큰으로 access 토큰 발급")
//	@GetMapping("/reissue")
//	public ResponseEntity<TokenResponse> reissue(@RequestParam(name = "refreshToken") String refreshToken) {
//		TokenResponse tokenResponse = appleOAuthClient.reissue(refreshToken);
//		return ResponseEntity.ok().body(tokenResponse);
//	}
}
