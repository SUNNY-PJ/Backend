package com.sunny.backend.auth;

import com.sunny.backend.auth.dto.AppleAuthClient;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
          makeClientSecret(),
          appleProperties.getGrantType(),
          authorizationCode
      ).getIdToken();

      return idToken;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  //feign 사용 x
//  public AppleIdTokenPayload getIdToken(String authorizationCode) {
//    String tokenUri = "https://appleid.apple.com/auth/token";
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//    body.add("client_id", appleProperties.getClientId());
//    body.add("client_secret", generateClientSecret());
//    body.add("grant_type", appleProperties.getGrantType());
//    body.add("code", authorizationCode);
//    log.info("body={}", body);
//    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
//
//    try {
//      ResponseEntity<String> responseEntity = restTemplate.exchange(tokenUri, HttpMethod.POST,
//          requestEntity, String.class);
//      log.info("responseEntity={}", responseEntity.getBody());
//      if (responseEntity.getStatusCode().is2xxSuccessful()) {
//        return TokenDecoder.decodePayload(responseEntity.getBody(), AppleIdTokenPayload.class);
//      } else {
//        return null;
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    return null;
//  }
//


//  public AppleIdTokenPayload get(String authorizationCode){
//    String clientSecret = generateClientSecret();
//    log.info("clientSecret={}", clientSecret);
//
//    AppleAuthRequest request = new AppleAuthRequest();
//    request.setClientId(appleProperties.getClientId());
//    request.setClientSecret(clientSecret);
//    request.setGrantType(appleProperties.getGrantType());
//    request.setCode(authorizationCode);
//    log.info("request={}", request);
//
//    AppleSocialTokenInfoResponse response = appleAuthClient.getIdToken(
//        request.getClientId(),
//        request.getClientSecret(),
//        request.getGrantType(),
//        request.getCode()
//    );
//
//    String idToken = response.getIdToken();
//    log.info("idToken={}", idToken);
//
//    return TokenDecoder.decodePayload(idToken, AppleIdTokenPayload.class);
//  }


//TODO 유효 기간 30일 이상으로 설정하면 에러 가능성 높음
private String generateClientSecret() {

  LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

  return Jwts.builder()
      .setHeaderParam(JwsHeader.KEY_ID, appleProperties.getKeyId())
      .setIssuer(appleProperties.getTeamId())
      .setAudience(appleProperties.getAudience())
      .setSubject(appleProperties.getClientId())
      .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
      .setIssuedAt(new Date())
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
