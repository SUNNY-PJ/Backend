package com.sunny.backend.security;

import java.util.Map;

import com.sunny.backend.security.exception.OAuth2AuthenticationProcessingException;
import com.sunny.backend.user.AuthProvider;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class OAuthAttributes {
	private String name;
	private String email;
	private String nameAttributeKey;
	private Map<String, Object> attributes;

	public static OAuthAttributes of(String registrationId, String nameAttributeKey, Map<String, Object> attributes) {
		if (registrationId.equalsIgnoreCase(AuthProvider.kakao.toString())) {
			return ofKakao(nameAttributeKey, attributes);
		}
		// else if (registrationId.equalsIgnoreCase(AuthProvider.naver.toString())) {
		// 	return ofNaver(nameAttributeKey, attributes);
		else {
			throw new OAuth2AuthenticationProcessingException(
				"Sorry! Login with " + registrationId + " is not supported yet.");
		}
	}

	private static OAuthAttributes ofKakao(String nameAttributeKey, Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

		return OAuthAttributes.builder()
			.name((String)kakaoProfile.get("nickname"))
			.email((String)kakaoAccount.get("email"))
			.attributes(attributes)
			.build();
	}

	// private static OAuthAttributes ofNaver(String nameAttributeKey, Map<String, Object> attributes) {
	// 	Map<String, Object> naverAccount = (Map<String, Object>)attributes.get("response");
	//
	// 	return OAuthAttributes.builder()
	// 		.name(naverAccount.get("name").toString())
	// 		.email(naverAccount.get("email").toString())
	// 		.nameAttributeKey(naverAccount.get("name").toString())
	// 		.attributes(attributes)
	// 		.build();
	// }

}
