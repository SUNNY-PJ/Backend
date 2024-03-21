package com.sunny.backend.auth.jwt;

import com.sunny.backend.auth.dto.UserRequest;
import com.sunny.backend.common.response.ResponseService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.util.ObjectUtils;


@Service
@RequiredArgsConstructor
public class TokenProvider {
	@Value("${app.auth.tokenSecret}")
	private String tokenSecret;
	@Value("${app.auth.tokenExpirationMsec}")
	private long tokenExpirationMsec;

	private final RedisUtil redisUtil;
	private final CustomUserDetailsService customUserDetailsService;
	private ResponseService responseService;
	private RedisTemplate redisTemplate;

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

		//TODO test용
		redisTemplate.opsForValue()
				.set("RT:" + email, refreshToken, getClaims(refreshToken).getExpiration().getTime(), TimeUnit.MILLISECONDS);

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

	public ResponseEntity<?> logout(UserRequest logout) {
		if (!validateToken(logout.getAccessToken())) {
			return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.");
		}
		Authentication authentication = getAuthentication(logout.getAccessToken());

		if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
			redisTemplate.delete("RT:" + authentication.getName());
		}
		//blacklist 처리
		Long expiration = getExpiration(logout.getAccessToken());
		redisTemplate.opsForValue()
				.set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

		return responseService.getGeneralResponse(HttpStatus.OK.value(), "로그아웃 성공입니다.");
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
	public ResponseEntity<?> reissue(UserRequest reissue,String email) {
		// Refresh Token 검증
		if (!validateToken(reissue.getRefreshToken())) {
			return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "Refresh Token 정보가 유효하지 않습니다.");
		}
		Authentication authentication = getAuthentication(reissue.getAccessToken());

		// Redis 에서 User email 을 기반으로 저장된 Refresh Token 값
		String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());
		// 로그아웃되어 Redis에 RefreshToken 이 존재하지 않는 경우 처리
		if(ObjectUtils.isEmpty(refreshToken)) {
			return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.");
		}
		if(!refreshToken.equals(reissue.getRefreshToken())) {
			return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "RefreshToken 정보가 잘못되었습니다.");
		}

		// 토큰 생성
		TokenResponse tokenInfo = createToken(email, authentication.getAuthorities().toString(),true);

		//RefreshToken Redis 업데이트
		redisTemplate.opsForValue()
				.set("RT:" + authentication.getName(), tokenInfo.refreshToken(),getClaims(tokenInfo.refreshToken()).getExpiration().getTime(), TimeUnit.MILLISECONDS);

		return responseService.getSingleResponse(HttpStatus.OK.value(), tokenInfo,"토큰 정복 갱신되었습니다.");
	}

}
