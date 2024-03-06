package com.sunny.backend.auth.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class AppleAuthRequest {
  private String clientId;
  private String clientSecret;
  private String grantType;
  private String code;
}

