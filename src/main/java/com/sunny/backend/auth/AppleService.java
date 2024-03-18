package com.sunny.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.backend.auth.dto.AppleAuthClient;
import com.sunny.backend.auth.dto.AppleTokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.exception.UserErrorCode;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.mapstruct.ap.shaded.freemarker.core.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleService {

  private final AppleAuthClient appleAuthClient;
  private final AppleProperties appleProperties;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final CommentNotificationRepository commentNotificationRepository;
  private final ResponseService responseService;

  public String generateClientSecret() throws IOException {
    LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);
    Map<String, Object> jwtHeader = new HashMap<>();
    jwtHeader.put("kid", appleProperties.getKeyId());
    jwtHeader.put("alg", "ES256");

    return Jwts.builder()
        .setHeaderParams(jwtHeader)
        .setIssuer(appleProperties.getTeamId())
        .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
        .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant())) // 만료 시간
        .setAudience("https://appleid.apple.com")
        .setSubject(appleProperties.getClientId())
        .signWith(SignatureAlgorithm.ES256, getPrivateKey())
        .compact();
  }


  private PrivateKey getPrivateKey() {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
    try {
      byte[] privateKeyBytes = Base64.getDecoder().decode(appleProperties.getPrivateKey());

      PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
      return converter.getPrivateKey(privateKeyInfo);
    } catch (Exception e) {
      throw new RuntimeException("Error converting private key from String", e);
    }
  }

  public ResponseEntity<CommonResponse.GeneralResponse> revoke(
      CustomUserPrincipal customUserPrincipal,
      String code){
    try{
      Users users=customUserPrincipal.getUsers();
      AppleRevokeRequest appleRevokeRequest=AppleRevokeRequest.builder()
          .client_id(appleProperties.getClientId())
          .client_secert(generateClientSecret())
          .token(code)
          .token_type_hint("access_token")
          .build();
      appleAuthClient.revoke(appleRevokeRequest);
      commentNotificationRepository.deleteByUsersId(users.getId());
      commentRepository.nullifyUsersId(users.getId());
      userRepository.deleteById(users.getId());
      return responseService.getGeneralResponse(HttpStatus.OK.value(), "탈퇴 성공");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public UserNameResponse changeNickname(CustomUserPrincipal customUserPrincipal, String name) {
    Users user = customUserPrincipal.getUsers();
    Optional<Users> optionalUsers = userRepository.findByNickname(name);
    if (optionalUsers.isPresent()) {
      throw new CustomException(UserErrorCode.NICKNAME_IN_USE);
    }
    user.updateName(name);
    userRepository.save(user);
    return new UserNameResponse(user.getNickname());
  }

}
