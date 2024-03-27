package com.sunny.backend.apple.dto.request;



import com.sunny.backend.common.config.AppleFeignClientConfiguration;
import com.sunny.backend.apple.publicKey.ApplePublicKeys;
import com.sunny.backend.apple.dto.response.AppleSocialTokenInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(value = "/revoke",consumes = "application/x-www-form-urlencoded" )
    void revoke(AppleRevokeRequest appleRevokeRequest);
  }

