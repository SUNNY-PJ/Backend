package com.sunny.backend.apple.service;

import com.sunny.backend.auth.dto.TokenResponse;

public interface OAuth2Client {
	TokenResponse getOAuthMemberId(String idToken);
}