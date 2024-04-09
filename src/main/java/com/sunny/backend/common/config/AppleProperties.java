package com.sunny.backend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "social-login.provider.apple")
@Getter
@Setter
public class AppleProperties {
	private String grantType;
	private String clientId;
	private String keyId;
	private String teamId;
	private String audience;
	private String privateKey;
}