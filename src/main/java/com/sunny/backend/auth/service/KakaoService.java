package com.sunny.backend.auth.service;

import static com.sunny.backend.common.CommonErrorCode.NICKNAME_IN_USE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.jwt.TokenProvider;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.auth.dto.OAuthToken;
import com.sunny.backend.common.response.CommonResponse;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoService {
    @Value("${custom_oauth2.client_id}")
    private String clientId;
    @Value("${custom_oauth2.redirect_uri}")
    private String redirectUri;

    private final RedisUtil redisUtil;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public String getAccessToken(String code) {
        RestTemplate rt = new RestTemplate();

        // HTTP POST를 요청할 때 보내는 데이터(body)를 설명해주는 헤더도 만들어 같이 보내줘야 한다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // body 데이터를 담을 오브젝트인 MultiValueMap를 만들어보자
        // body는 보통 key, value의 쌍으로 이루어지기 때문에 자바에서 제공해주는 MultiValueMap 타입을 사용한다.
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // 요청하기 위해 헤더(Header)와 데이터(Body)를 합친다.
        // kakaoTokenRequest는 데이터(Body)와 헤더(Header)를 Entity가 된다.
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // POST 방식으로 Http 요청한다. 그리고 response 변수의 응답 받는다.
        ResponseEntity<OAuthToken> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST, // 요청할 방식
                kakaoTokenRequest, // 요청할 때 보낼 데이터
            OAuthToken.class // 요청 시 반환되는 데이터 타입
        );

        return response.getBody().getAccessToken();
    }

    public TokenResponse getEmailForUserInfo(String code) throws Exception {
        String accessToken = getAccessToken(code);
        String host = "https://kapi.kakao.com/v2/user/me";
        URL url = new URL(host);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        urlConnection.setRequestMethod("GET");

        String email;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            String line = "";
            String res = "";
            while ((line = br.readLine()) != null) {
                res += line;
            }
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject)parser.parse(res);
            JSONObject kakao_account = (JSONObject)obj.get("kakao_account");
            JSONObject properties = (JSONObject)obj.get("properties");

            email = kakao_account.get("email").toString();
            String nickname = properties.get("nickname").toString();
            String profileImg = properties.get("profile_image").toString();
            String defaultImageUrl = "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg";
            if (profileImg.equals(defaultImageUrl)) {
                profileImg = "https://sunny-pj.s3.ap-northeast-2.amazonaws.com/Profile+Image.png";
            }
            Optional<Users> usersOptional = userRepository.findByEmail(email);
            if (usersOptional.isEmpty()) {
                Users users = Users.builder()
                    .email(email)
                    .name(nickname)
                    .profile(profileImg)
                    .role(Role.USER)
                    .build();
                userRepository.save(users);
            }
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
        userRepository.deleteById(users.getId());
    }
}