package com.sunny.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.backend.auth.dto.AppleAuthClient;
import com.sunny.backend.auth.dto.AppleTokenResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

  @Autowired
  private RestTemplate restTemplate;

  public String getIdToken(String authorizationCode) {

    try {
      String idToken = appleAuthClient.getIdToken(
          appleProperties.getClientId(),
          generateClientSecret(),
          appleProperties.getGrantType(),
          authorizationCode
      ).getIdToken();
      return idToken;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public HashMap<String, Object> generateAuthToken(String authorizationCode) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplateBuilder().build();
    HashMap<String, Object> rtnMap = new HashMap<String, Object>();
    String authUrl = "https://appleid.apple.com/auth/token";

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", authorizationCode);
    params.add("client_id", appleProperties.getClientId());
    params.add("client_secret", generateClientSecret());
    params.add("grant_type", "authorization_code");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(authUrl, httpEntity, String.class);
      HashMap respMap = objectMapper.readValue(response.getBody(), HashMap.class);

      rtnMap.put("statusCode", response.getStatusCodeValue());
      rtnMap.put("accessToken", respMap.get("access_token"));
      rtnMap.put("refreshToken", respMap.get("refresh_token"));
      rtnMap.put("idToken", respMap.get("id_token"));
      rtnMap.put("expiresIn", respMap.get("expires_in"));

      return rtnMap;
    } catch (HttpClientErrorException e) {
      log.error(String.valueOf(e));
      log.error("Apple Auth Token Error");
      HashMap respMap = objectMapper.readValue(e.getResponseBodyAsString(), HashMap.class);
      rtnMap.put("statusCode", e.getRawStatusCode());
      rtnMap.put("errorDescription", respMap.get("error_description"));
      rtnMap.put("error", respMap.get("error"));

      return rtnMap;
    }
  }

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
  public String makeClientSecret() throws IOException {
    Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
    return Jwts.builder()
        .setHeaderParam("kid", appleProperties.getKeyId())
        .setHeaderParam("alg", "ES256")
        .setIssuer(appleProperties.getTeamId())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(expirationDate)
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

}
