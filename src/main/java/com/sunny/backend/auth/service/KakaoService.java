package com.sunny.backend.auth.service;

import static com.sunny.backend.common.CommonErrorCode.NICKNAME_IN_USE;
import static com.sunny.backend.common.ComnConstant.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sunny.backend.auth.dto.KakaoIdResponse;
import com.sunny.backend.auth.dto.KakaoMemberResponse;
import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.jwt.TokenProvider;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.auth.dto.OAuthToken;
import com.sunny.backend.user.domain.Role;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

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
            Users users = Users.builder()
                .email(email)
                .name(kakaoAccount.getProfile().getNickname())
                .profile(kakaoAccount.getProfile().getProfileImageUrl())
                .role(Role.USER)
                .oauthId(String.valueOf(kakaoMemberResponse.getId()))
                .build();
            userRepository.save(users);
        }

        return tokenProvider.createToken(email, "ROLE_USER");
    }

    @Transactional
    public UserNameResponse changeNickname(CustomUserPrincipal customUserPrincipal, String name){
        Users user = customUserPrincipal.getUsers();
        Optional<Users> optionalUsers = userRepository.findByName(name);
        if (optionalUsers.isPresent()) {
            throw new CommonCustomException(NICKNAME_IN_USE);
        }
        user.setName(name);
        return new UserNameResponse(user.getName());
    }

    @Transactional
    public void leave(CustomUserPrincipal customUserPrincipal) {
        Users users = customUserPrincipal.getUsers();
        appAdminKeyMethod(users.getOauthId(), KAKAO_LEAVE_URL);
        userRepository.deleteById(users.getId());
    }

    public void logout(CustomUserPrincipal customUserPrincipal) {
        Users users = customUserPrincipal.getUsers();
        appAdminKeyMethod(users.getOauthId(), KAKAO_LOGOUT_URL);
    }

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
                HttpMethod.POST, // 요청할 방식
                kakaoTokenRequest, // 요청할 때 보낼 데이터
                KakaoIdResponse.class // 요청 시 반환되는 데이터 타입
        );
    }

}