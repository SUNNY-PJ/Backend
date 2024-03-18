package com.sunny.backend.auth;

import static com.sunny.backend.common.ComnConstant.KAKAO_USER_URL;

import com.sunny.backend.auth.dto.AppleAuthClient;
import com.sunny.backend.auth.dto.KakaoIdResponse;
import com.sunny.backend.auth.dto.KakaoMemberResponse;
import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.exception.UserErrorCode;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.auth.jwt.TokenProvider;
import com.sunny.backend.auth.service.CustomUserDetailsService;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.user.domain.Role;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOAuthClient implements OAuth2Client {
  private final JwtParser jwtParser;
  private final AppleApiCaller appleApiCaller;
  private final AppleOAuthPublicKeyGenerator appleOAuthPublicKeyGenerator;

  @Value("${app.auth.tokenSecret}")
  private String tokenSecret;
  @Value("${app.auth.tokenExpirationMsec}")
  private long tokenExpirationMsec;

  private final CommentRepository commentRepository;
  private final CommentNotificationRepository commentNotificationRepository;
  private final UserRepository userRepository;
  private final TokenProvider tokenProvider;
  private final AppleAuthClient appleAuthClient;
  private final AppleProperties appleProperties;


  @Transactional
  public TokenResponse getOAuthMemberIdTest(String idToken,String code) {

    ApplePublicKeys applePublicKeys = appleAuthClient.getAppleAuthPublicKey();
    System.out.println(applePublicKeys.getKeys().size());
    Map<String, String> headers = jwtParser.parseHeaders(idToken);
    PublicKey publicKey = appleOAuthPublicKeyGenerator.generatePublicKey(headers,
        applePublicKeys);
    Claims claims = jwtParser.parseClaims(idToken, publicKey);
    String oAuthId = claims.getSubject();
    String email = claims.get("email", String.class);
    Optional<Users> usersOptional = userRepository.findByEmail(email);
    if (usersOptional.isEmpty()) {
      Users users = Users.builder()
          .email(email)
          .oauthId(oAuthId)
          .role(Role.USER)
          .build();
      userRepository.save(users);
      return tokenProvider.createToken(email, Role.USER.getRole(),false);
    } else {
      return tokenProvider.createToken(email, usersOptional.get().getRole().getRole(),true);
    }
  }
  @Override
  @Transactional
  public TokenResponse getOAuthMemberId(String idToken) {

    ApplePublicKeys applePublicKeys = appleAuthClient.getAppleAuthPublicKey();
    System.out.println(applePublicKeys.getKeys().size());
    Map<String, String> headers = jwtParser.parseHeaders(idToken);
    PublicKey publicKey = appleOAuthPublicKeyGenerator.generatePublicKey(headers,
        applePublicKeys);

    Claims claims = jwtParser.parseClaims(idToken, publicKey);
    String oAuthId = claims.getSubject();
    String email = claims.get("email", String.class);
    Optional<Users> usersOptional = userRepository.findByEmail(email);
    if (usersOptional.isEmpty()) {
      Users users = Users.builder()
          .email(email)
          .oauthId(oAuthId)
          .role(Role.USER)
          .build();
      userRepository.save(users);
      return tokenProvider.createToken(email, Role.USER.getRole(),false);
    } else {
      return tokenProvider.createToken(email, usersOptional.get().getRole().getRole(),true);
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

	@Transactional
	public void leave(CustomUserPrincipal customUserPrincipal) {
		Users users = customUserPrincipal.getUsers();
		commentNotificationRepository.deleteByUsersId(users.getId());
		commentRepository.nullifyUsersId(users.getId());
		userRepository.deleteById(users.getId());
	}


  private void validateClaims(Claims claims) {
    // 클레임에서 필요한 정보 추출
    String subject = claims.getSubject(); // 서브젝트
    Date expiration = claims.getExpiration(); // 만료일
    String issuer = claims.getIssuer(); // 발급자
    // 클레임 유효성 검사
    if (subject == null || subject.isEmpty()) {
      throw new UnauthorizedException("Invalid subject in JWT claims");
    }

    if (expiration == null || expiration.before(new Date())) {
      throw new UnauthorizedException("JWT token has expired");
    }

    if (issuer == null || !issuer.equals("apple")) {
      throw new UnauthorizedException("Invalid issuer in JWT claims");
    }
  }
  public Claims getClaims(String token) {
    return Jwts.parser()
        .setSigningKey(tokenSecret)
        .parseClaimsJws(token)
        .getBody();
  }

  //시작

  public String getIdToken(String authorizationCode) {
    log.info("호출");
    try {
      String idToken = appleAuthClient.getIdToken(
          appleProperties.getClientId(),
          createClientSecret(),
          appleProperties.getGrantType(),
          authorizationCode
      ).getIdToken();
      return idToken;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String createClientSecret() throws IOException {
    Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
    Map<String, Object> jwtHeader = new HashMap<>();
    jwtHeader.put("kid", "R76G46JCNL"); // kid
    jwtHeader.put("alg", "ES256"); // alg
    String jwt= Jwts.builder()
        .setHeaderParams(jwtHeader)
        .setIssuer("8L6HR8J2B8") // iss
        .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간
        .setExpiration(expirationDate) // 만료 시간
        .setAudience("https://appleid.apple.com") // aud
        .setSubject("com.sunnymoney2024.SUNNY") // sub
        .signWith(SignatureAlgorithm.ES256, getPrivateKey())
        .compact();
    log.info("jwt={}",jwt);
    return jwt;
  }

  public PrivateKey getPrivateKey() throws IOException {
    ClassPathResource resource = new ClassPathResource("static/AuthKey_R76G46JCNL.p8"); // .p8 key파일 위치
    String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));

//    File f = new File("C:/workspace/AuthKey_1234ABCD.p8");
//    String privateKey = new String(Files.readAllBytes(f.toPath()));

    Reader pemReader = new StringReader(privateKey);
    PEMParser pemParser = new PEMParser(pemReader);
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
    PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
    return converter.getPrivateKey(object);
  }


}


