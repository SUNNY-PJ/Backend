package com.sunny.backend.apple.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleRevokeRequest {
	private String client_id;
	private String client_secert;
	private String token;
	private String token_type_hint;
}
