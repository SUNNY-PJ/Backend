package com.sunny.backend.auth.service;

import static com.sunny.backend.common.ComnConstant.*;

import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.sunny.backend.auth.dto.KakaoIdResponse;
import com.sunny.backend.auth.dto.KakaoMemberResponse;
import com.sunny.backend.auth.dto.KakaoRequest;
import com.sunny.backend.auth.dto.OAuthToken;
import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.exception.UserErrorCode;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.auth.jwt.TokenProvider;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.user.domain.Role;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoService {
	@Value("${custom_oauth2.clientId}")
	private String clientId;
	@Value("${custom_oauth2.redirectUri}")
	private String redirectUri;
	@Value("${custom_oauth2.adminKey}")
	private String adminKey;

	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final CommentNotificationRepository commentNotificationRepository;

	public TokenResponse kakaoLogin(KakaoRequest kakaoRequest) {
		String email = kakaoRequest.getEmail();
		String progile = kakaoRequest.getProfile();

		Optional<Users> usersOptional = userRepository.findByEmail(email);
		if (usersOptional.isEmpty()) {
			Users users = Users.of(email, String.valueOf(kakaoRequest.getId()));
			userRepository.save(users);
			TokenResponse t = tokenProvider.createToken(email, Role.USER.getRole(), false);
			return tokenProvider.createToken(email, Role.USER.getRole(), false);

		} else {
			return tokenProvider.createToken(email, usersOptional.get().getRole().getRole(), true);
		}
	}

	public String getAccessToken(String code) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		ResponseEntity<OAuthToken> response = rt.exchange(
			"https://kauth.kakao.com/oauth/token",
			HttpMethod.POST,
			new HttpEntity<>(params, headers),
			OAuthToken.class
		);
		return Objects.requireNonNull(response.getBody()).getAccessToken();
	}

	public TokenResponse getEmailForUserInfo(String code) {
		String kakaoAccessToken = getAccessToken(code);
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + kakaoAccessToken);

		ResponseEntity<KakaoMemberResponse> response = rt.exchange(
			KAKAO_USER_URL,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			KakaoMemberResponse.class
		);

		KakaoMemberResponse kakaoMemberResponse = response.getBody();
		KakaoMemberResponse.KakaoAccount kakaoAccount = kakaoMemberResponse.getKakaoAccount();
		String email = kakaoAccount.getEmail();
		kakaoAccount.getProfile().checkDefaultImage();

		Optional<Users> usersOptional = userRepository.findByEmail(email);
		if (usersOptional.isEmpty()) {
			Users users = Users.of(email, String.valueOf(kakaoMemberResponse.getId()));
			userRepository.save(users);
			return tokenProvider.createToken(email, Role.USER.getRole(), false);
		} else {
			return tokenProvider.createToken(email, usersOptional.get().getRole().getRole(), true);
		}
	}

	@Transactional
	public UserNameResponse changeNickname(CustomUserPrincipal customUserPrincipal, String name) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Optional<Users> optionalUsers = userRepository.findByNickname(name);
		if (optionalUsers.isPresent()) {
			throw new CustomException(UserErrorCode.NICKNAME_IN_USE);
		}
		user.updateName(name);
		userRepository.save(user);
		return new UserNameResponse(user.getNickname());
	}

	// @Transactional
	// public void leave(CustomUserPrincipal customUserPrincipal) {
	// 	Users users = customUserPrincipal.getUsers();
	// 	commentNotificationRepository.deleteByUsersId(users.getId());
	// 	commentRepository.nullifyUsersId(users.getId());
	// 	userRepository.deleteById(users.getId());
	// }
	//
	// public void logout(CustomUserPrincipal customUserPrincipal) {
	// 	Users users = customUserPrincipal.getUsers();
	// 	appAdminKeyMethod(users.getOauthId(), KAKAO_LOGOUT_URL);
	// }

	public void appAdminKeyMethod(String oauthId, String url) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add("Authorization", "KakaoAK " + adminKey);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("target_id_type", "user_id");
		params.add("target_id", oauthId);

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
		ResponseEntity<KakaoIdResponse> response = rt.exchange(
			url,
			HttpMethod.POST,
			kakaoTokenRequest,
			KakaoIdResponse.class
		);
	}

	public TokenResponse getEmailForUserInfoByKakaoAccessToken(String kakaoAccessToken) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + kakaoAccessToken);

		ResponseEntity<KakaoMemberResponse> response = rt.exchange(
			KAKAO_USER_URL,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			KakaoMemberResponse.class
		);

		KakaoMemberResponse kakaoMemberResponse = response.getBody();
		KakaoMemberResponse.KakaoAccount kakaoAccount = kakaoMemberResponse.getKakaoAccount();
		String email = kakaoAccount.getEmail();
		kakaoAccount.getProfile().checkDefaultImage();

		Optional<Users> usersOptional = userRepository.findByEmail(email);
		if (usersOptional.isEmpty()) {
			Users users = Users.of(email, String.valueOf(kakaoMemberResponse.getId()));
			userRepository.save(users);
			return tokenProvider.createToken(email, Role.USER.getRole(), false);
		} else {
			return tokenProvider.createToken(email, usersOptional.get().getRole().getRole(), true);
		}
	}
}