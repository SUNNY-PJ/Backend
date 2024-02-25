//package com.sunny.backend.auth.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor
//@Service
//@Slf4j
//public class UserOAuth2Service extends DefaultOAuth2UserService {
//
//  @Override
//  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//    String registrationId = userRequest.getClientRegistration().getRegistrationId();
//
//    //access token을 이용해 provider 서버로부터 사용자 정보를 받아온다.
//    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//
//    //Apple의 경우 id_token에 회원정보가 있으므로 회원정보 API 호출과정 생략
//    Map<String, Object> attributes;
//    if(registrationId.contains("apple")){
//      String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
//      attributes = decodeJwtTokenPayload(idToken);
//      attributes.put("id_token", idToken);
//    }else{
//      OAuth2User oAuth2User = delegate.loadUser(userRequest);
//      attributes = oAuth2User.getAttributes();
//    }
//
//    //provider에서 넘어온 회원 정보에서 필요한 파라미터만 추출해 DTO로 변환
//    OAuthAttributes customAttributes = OAuthAttributes.of(registrationId, attributes);
//
//    return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
//        customAttributes.getAttributes(),
//        customAttributes.getNameAttributeKey());
//  }
//
//  //JWT Payload부분 decode 메서드
//  public Map<String, Object> decodeJwtTokenPayload(String jwtToken){
//    Map<String, Object> jwtClaims = new HashMap<>();
//    try {
//      String[] parts = jwtToken.split("\\.");
//      Base64.Decoder decoder = Base64.getUrlDecoder();
//
//      byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
//      String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
//      ObjectMapper mapper = new ObjectMapper();
//
//      Map<String, Object> map = mapper.readValue(decodedString, Map.class);
//      jwtClaims.putAll(map);
//
//    } catch (JsonProcessingException e) {
//      log.error("decodeJwtToken: {}-{} / jwtToken : {}", e.getMessage(), e.getCause(), jwtToken);
//    }
//    return jwtClaims;
//  }
//}