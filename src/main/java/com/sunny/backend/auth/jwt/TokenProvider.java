package com.sunny.backend.auth.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.dto.UserRequest;
import com.sunny.backend.auth.service.CustomUserDetailsService;
import com.sunny.backend.util.RedisUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {
	@Value("${app.auth.tokenSecret}")
	private String tokenSecret;
	@Value("${app.auth.tokenExpirationMsec}")
	private long tokenExpirationMsec;

	private final RedisUtil redisUtil;

	private final CustomUserDetailsService customUserDetailsService;

	public TokenResponse createToken(String email, String authorities, boolean isUserRegistered) {
		String accessToken = createAccessToken(email, authorities);
		String refreshToken = createRefreshToken(email);
		return new TokenResponse(accessToken, refreshToken, isUserRegistered);
	}

	public String createAccessToken(String email, String authorities) {
		String accessToken = Jwts.builder()
			.setHeader(createHeader())
			.claim("email", email)
			.claim("authorities", authorities)
			.setSubject("accessToken")
			.setExpiration(createExpiredDate(1))
			.signWith(SignatureAlgorithm.HS512, tokenSecret)
			.compact();

		return accessToken;
	}

	public String createRefreshToken(String email) {
		String refreshToken = Jwts.builder()
			.setHeader(createHeader())
			.setSubject("refreshToken")
			.setExpiration(createExpiredDate(2 * 7 * 24))
			.signWith(SignatureAlgorithm.HS512, tokenSecret)
			.compact();
		redisUtil.setValuesWithTimeout(refreshToken, email, getClaims(refreshToken).getExpiration().getTime());

		return refreshToken;
	}

	public Claims getClaims(String token) {
		return Jwts.parser()
			.setSigningKey(tokenSecret)
			.parseClaimsJws(token)
			.getBody();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
			return true;
		} catch (SignatureException e) { // 유효하지 않은 JWT 서명
			throw new RuntimeException("not valid jwt signature");
		} catch (MalformedJwtException e) { // 유효하지 않은 JWT
			throw new RuntimeException("not valid jwt");
		} catch (io.jsonwebtoken.ExpiredJwtException e) { // 만료된 JWT
			throw new RuntimeException("expired jwt");
		} catch (io.jsonwebtoken.UnsupportedJwtException e) { // 지원하지 않는 JWT
			throw new RuntimeException("unsupported jwt");
		} catch (IllegalArgumentException e) { // 빈값
			throw new RuntimeException("empty jwt");
		}
	}

	private Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<>();
		header.put("typ", "JWT");
		header.put("alg", "HS256");
		header.put("regDate", System.currentTimeMillis());
		return header;
	}

	private Date createExpiredDate(int time) {
		Date now = new Date();
		return new Date(now.getTime() + tokenExpirationMsec * time);
	}

	public Authentication getAuthentication(String token) {
		String email = getClaims(token).get("email").toString();
		CustomUserPrincipal userDetails = customUserDetailsService.loadUserByUsername(email);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	public boolean logout(UserRequest logout) {
		if (!validateToken(logout.getAccessToken())) {
			return false;
		}
		if (redisUtil.getRefreshToken(logout.getRefreshToken()) != null) {
			log.info("logout refresh={}", logout.getRefreshToken());
			redisUtil.deleteRefreshToken(logout.getRefreshToken());
		}
		//blacklist 처리
		Long expiration = getExpiration(logout.getAccessToken());
		redisUtil.setValuesWithTimeout(logout.getAccessToken(), "logout", expiration);

		return true;
	}

	public Long getExpiration(String accessToken) {
		// accessToken 남은 유효시간
		Date expiration =
			Jwts.parser()
				.setSigningKey(tokenSecret)
				.parseClaimsJws(accessToken)
				.getBody().getExpiration();
		// 현재 시간
		Long now = new Date().getTime();
		return (expiration.getTime() - now);
	}

	public ResponseEntity<?> reissue(String reissueRefreshToken, String email) {
		// Refresh Token 검증
		if (!validateToken(reissueRefreshToken)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Refresh Token 정보가 유효하지 않습니다. 재로그인을 시도해주세요.");
		}
		// Redis 에서 User email 을 기반으로 저장된 Refresh Token 값
		String refreshToken = redisUtil.getRefreshToken(reissueRefreshToken);
		// 로그아웃되어 Redis에 RefreshToken 이 존재하지 않는 경우 처리
		if (ObjectUtils.isEmpty(refreshToken)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Refresh Token이 존재하지 않습니다. 재로그인을 시도해주세요. ");
		}
		if (!refreshToken.equals(reissueRefreshToken)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("RefreshToken 정보가 잘못되었습니다.");
		}
		// 토큰 생성
		String accessToken = createAccessToken(email, "ROLE_USER");
		TokenResponse tokenInfo = new TokenResponse(accessToken, refreshToken, true);
		return ResponseEntity.ok(tokenInfo);
	}

}

