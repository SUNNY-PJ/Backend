package com.sunny.backend.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "social-login.provider.apple")
@Getter
@Setter
public class AppleProperties {
  //TODO 사전에 필요한 애플 설정

  private String grantType;
  private String clientId;
  private String keyId;
  private String teamId;
  private String audience;
  private String privateKey;
}