//package com.sunny.backend.auth.dto;
//
//
//import com.sunny.backend.auth.AppleFeignClientConfig;
//import com.sunny.backend.auth.AppleSocialTokenInfoResponse;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@FeignClient(name="appleClient",url="https://appleid.apple.com/auth", configuration = AppleFeignClientConfig.class)
//public interface AppleAuthClient {
//  @PostMapping("/auth/token")
//  AppleSocialTokenInfoResponse getIdToken(
//      @RequestParam("client_id") String clientId,
//      @RequestParam("client_secret") String clientSecret,
//      @RequestParam("grant_type") String grantType,
//      @RequestParam("code") String code
//  );
//}
