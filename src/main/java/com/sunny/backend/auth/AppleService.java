package com.sunny.backend.auth;

import com.sunny.backend.auth.dto.AppleAuthClient;
import com.sunny.backend.auth.dto.AppleAuthRequest;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleService {
  private final AppleAuthClient appleAuthClient;
  private final AppleProperties appleProperties;

  public AppleIdTokenPayload get(String authorizationCode){
    AppleAuthRequest request = new AppleAuthRequest();
    request.setClientId(appleProperties.getClientId());
    request.setClientSecret(generateClientSecret());
    request.setGrantType(appleProperties.getGrantType());
    request.setCode(authorizationCode);
    log.info("request={}",request);
    AppleSocialTokenInfoResponse response = appleAuthClient.getIdToken(request);
    String idToken = response.getIdToken();
    System.out.println(idToken);
    return TokenDecoder.decodePayload(idToken, AppleIdTokenPayload.class);
  }

//TODO 유효 기간 30일 이상으로 설정하면 에러 가능성 높음, 테스트 해보고 여기서 오류난다면 수정해야 할 듯
  private String generateClientSecret(){
    LocalDateTime expiration=LocalDateTime.now().plusMinutes(5);
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
