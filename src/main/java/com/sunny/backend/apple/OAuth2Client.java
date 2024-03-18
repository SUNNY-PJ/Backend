package com.sunny.backend.apple;

import com.sunny.backend.auth.dto.TokenResponse;

public interface OAuth2Client {
  TokenResponse getOAuthMemberId(String idToken);
}