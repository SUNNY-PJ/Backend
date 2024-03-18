package com.sunny.backend.auth;

import com.sunny.backend.auth.dto.TokenResponse;

// OAuth2Client.java
public interface OAuth2Client {
  TokenResponse getOAuthMemberId(String idToken);
}