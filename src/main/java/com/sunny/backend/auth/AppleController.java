package com.sunny.backend.auth;


import io.swagger.annotations.ApiOperation;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @ApiOperation(tags = "0. Auth", value = "애플 로그인 callback")
  @GetMapping("/auth/callback")
  public void appleCallback(@RequestParam("code") String code) throws IOException {
    log.info("apple callback method 호출");
    appleService.getIdToken(code);
  }

}

