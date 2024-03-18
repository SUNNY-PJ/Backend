package com.sunny.backend.auth.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.service.CustomUserDetailsService;
import com.sunny.backend.util.RedisUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class TokenProvider {
	@Value("${app.auth.tokenSecret}")
	private String tokenSecret;
	@Value("${app.auth.tokenExpirationMsec}")
	private long tokenExpirationMsec;

	private final RedisUtil redisUtil;
	private final CustomUserDetailsService customUserDetailsService;

	public TokenResponse createToken(String email, String authorities,boolean isUserRegistered) {
		String accessToken = Jwts.builder()
			.setHeader(createHeader())
			.claim("email", email)
			.claim("authorities", authorities)
			.setSubject("accessToken")
			.setExpiration(createExpiredDate(1))
			.signWith(SignatureAlgorithm.HS512, tokenSecret)
			.compact();
		System.out.println("accessToken "+accessToken);
		String refreshToken = Jwts.builder()
			.setHeader(createHeader())
			.setSubject("refreshToken")
			.setExpiration(createExpiredDate(2 * 7 * 24))
			.signWith(SignatureAlgorithm.HS512, tokenSecret)
			.compact();

		redisUtil.setValuesWithTimeout(refreshToken, email, getClaims(refreshToken).getExpiration().getTime());

		return new TokenResponse(accessToken, refreshToken,isUserRegistered);
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
}
