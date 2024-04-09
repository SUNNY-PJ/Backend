package com.sunny.backend.apple.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sunny.backend.apple.publicKey.ApplePublicKeys;
import com.sunny.backend.common.config.AppleFeignClientConfiguration;

@Component
@FeignClient(
	name = "apple-auth",
	url = "${client.apple-auth.url}",
	configuration = AppleFeignClientConfiguration.class
)
public interface AppleAuthClient {

	@GetMapping(value = "/keys")
	ApplePublicKeys getAppleAuthPublicKey();

	@PostMapping(value = "/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	ResponseEntity<String> revokeToken(@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("token") String token,
		@RequestParam("token_type_hint") String tokenTypeHint);
}




