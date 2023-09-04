package com.sunny.backend.security.service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import com.sunny.backend.user.Users;
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

		Optional<Users> optionalUser = userRepository.findByEmail(attributes.getEmail());
		Users users;
		if (optionalUser.isPresent()) {
			users = optionalUser.get();
			if (!users.getAuthProvider()
				.equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
				throw new OAuth2AuthenticationProcessingException(
					"Looks like you're signed up with " + users.getAuthProvider() + " account. Please use your "
						+ users.getAuthProvider() + " account to login.");
			}
			users = updateUser(users, attributes);
		} else {
			users = registerUser(oAuth2UserRequest, attributes);
		}

		return CustomUserPrincipal.create(users, oAuth2User.getAttributes());
	}

	private Users registerUser(OAuth2UserRequest oAuth2UserRequest, OAuthAttributes attributes) {
		Users users = Users.builder()
			.authProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
			.providerId(attributes.getNameAttributeKey())
			.name(attributes.getName())
			.email(attributes.getEmail())
			.role(Role.USER)
			.build();

		return userRepository.save(users);
	}

	@Transactional
	private Users updateUser(Users users, OAuthAttributes attributes) {
		users.setName(attributes.getName());
		users.setUpdatedDate(LocalDateTime.now());
		return userRepository.save(users);
	}
}
