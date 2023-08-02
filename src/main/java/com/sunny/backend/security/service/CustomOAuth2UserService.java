package com.sunny.backend.security.service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.sunny.backend.security.OAuthAttributes;
import com.sunny.backend.security.exception.OAuth2AuthenticationProcessingException;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.AuthProvider;
import com.sunny.backend.user.Role;
import com.sunny.backend.user.User;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

		try {
			return processOAuth2User(oAuth2UserRequest, oAuth2User);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			// Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}

	}

	private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		OAuthAttributes attributes = OAuthAttributes.of(oAuth2UserRequest.getClientRegistration().getRegistrationId(),
			oAuth2UserRequest.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName(),
			oAuth2User.getAttributes());

		if (attributes.getEmail() == null) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}

		Optional<User> optionalUser = userRepository.findByEmail(attributes.getEmail());
		User user;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
			if (!user.getAuthProvider()
				.equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
				throw new OAuth2AuthenticationProcessingException(
					"Looks like you're signed up with " + user.getAuthProvider() + " account. Please use your "
						+ user.getAuthProvider() + " account to login.");
			}
			user = updateUser(user, attributes);
		} else {
			user = registerUser(oAuth2UserRequest, attributes);
		}

		return CustomUserPrincipal.create(user, oAuth2User.getAttributes());
	}

	private User registerUser(OAuth2UserRequest oAuth2UserRequest, OAuthAttributes attributes) {
		User user = User.builder()
			.authProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
			.providerId(attributes.getNameAttributeKey())
			.name(attributes.getName())
			.email(attributes.getEmail())
			.role(Role.USER)
			.build();

		return userRepository.save(user);
	}

	@Transactional
	private User updateUser(User user, OAuthAttributes attributes) {
		user.setName(attributes.getName());
		user.setUpdatedDate(LocalDateTime.now());
		return userRepository.save(user);
	}
}
