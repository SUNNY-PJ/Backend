package com.sunny.backend.auth.dto;



import com.sunny.backend.apple.AppleFeignClientConfiguration;
import com.sunny.backend.apple.ApplePublicKeys;
import com.sunny.backend.apple.AppleRevokeRequest;
import com.sunny.backend.apple.AppleSocialTokenInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    AppleSocialTokenInfoResponse getIdToken(
        @RequestParam("client_id") String clientId,
        @RequestParam("client_secret") String clientSecret,
        @RequestParam("grant_type") String grantType,
        @RequestParam("code") String code
    );

    @PostMapping(value = "/revoke",consumes = "application/x-www-form-urlencoded" )
    void revoke(AppleRevokeRequest appleRevokeRequest);

    @PostMapping(value = "/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> revokeToken(@RequestParam("client_id") String clientId,
        @RequestParam("client_secret") String clientSecret,
        @RequestParam("token") String token,
        @RequestParam("token_type_hint") String tokenTypeHint);
}




