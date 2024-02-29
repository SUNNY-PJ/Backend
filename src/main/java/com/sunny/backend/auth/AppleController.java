package com.sunny.backend.auth;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apple")
@RequiredArgsConstructor
public class AppleController {
  private final GetMemberInfoService getMemberInfoService;

  @ApiOperation(tags = "0. Auth", value = "애플 로그인 callback")
  @GetMapping("/auth/callback")
  public AppleIdTokenPayload appleCallback(String code) {
    return getMemberInfoService.get(code);
  }
}
