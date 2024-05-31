package com.sunny.backend.apple.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.apple.jwt.JwtParser;
import com.sunny.backend.apple.publicKey.AppleOAuthPublicKeyGenerator;
import com.sunny.backend.apple.publicKey.ApplePublicKeys;
import com.sunny.backend.auth.UnauthorizedException;
import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.jwt.TokenProvider;
import com.sunny.backend.user.domain.Role;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOAuthClient implements OAuth2Client {
	private final JwtParser jwtParser;
	private final AppleOAuthPublicKeyGenerator appleOAuthPublicKeyGenerator;

	@Value("${app.auth.tokenSecret}")
	private String tokenSecret;
	@Value("${app.auth.tokenExpirationMsec}")
	private long tokenExpirationMsec;
	private final UserRepository userRepository;
	private final TokenProvider tokenProvider;
	private final AppleAuthClient appleAuthClient;

	@Override
	@Transactional
	public TokenResponse getOAuthMemberId(String idToken) {
		ApplePublicKeys applePublicKeys = appleAuthClient.getAppleAuthPublicKey();
		Map<String, String> headers = jwtParser.parseHeaders(idToken);
		PublicKey publicKey = appleOAuthPublicKeyGenerator.generatePublicKey(headers,
			applePublicKeys);

		Claims claims = jwtParser.parseClaims(idToken, publicKey);
		String oAuthId = claims.getSubject();
		String email = claims.get("email", String.class);
		Optional<Users> usersOptional = userRepository.findByEmail(email);
		if (usersOptional.isEmpty()) {
			Users users = Users.of(email, oAuthId);
			userRepository.save(users);
			return tokenProvider.createToken(email, Role.USER.getRole(), false);
		} else {
			return tokenProvider.createToken(email, usersOptional.get().getRole().getRole(), true);
		}
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

	public PrivateKey getPrivateKey() throws IOException {
		ClassPathResource resource = new ClassPathResource("static/AuthKey_R76G46JCNL.p8"); // .p8 key파일 위치
		String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));

		Reader pemReader = new StringReader(privateKey);
		PEMParser pemParser = new PEMParser(pemReader);
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		PrivateKeyInfo object = (PrivateKeyInfo)pemParser.readObject();
		return converter.getPrivateKey(object);
	}
}


