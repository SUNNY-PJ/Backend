package com.sunny.backend.auth.dto;



import com.sunny.backend.auth.AppleFeignClientConfiguration;
import com.sunny.backend.auth.ApplePublicKey;
import com.sunny.backend.auth.ApplePublicKeys;
import com.sunny.backend.auth.AppleSocialTokenInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
@Component
@FeignClient(
    name = "apple-auth",
    url = "${client.apple-auth.url}",
    configuration = AppleFeignClientConfiguration.class
)
public interface AppleAuthClient {

    @GetMapping(value = "/keys")
    ApplePublicKeys getAppleAuthPublicKey();



    @PostMapping(value = "/auth/token", consumes = "application/x-www-form-urlencoded")
    AppleSocialTokenInfoResponse getIdToken(
        @RequestParam("client_id") String clientId,
        @RequestParam("client_secret") String clientSecret,
        @RequestParam("grant_type") String grantType,
        @RequestParam("code") String code
    );
  }
//public interface AppleAuthClient {
//  @PostMapping("/auth/token")
//  AppleSocialTokenInfoResponse getIdToken(AppleAuthRequest request);
//}