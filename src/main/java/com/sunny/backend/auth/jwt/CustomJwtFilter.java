package com.sunny.backend.auth.jwt;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sunny.backend.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {
	private final TokenProvider tokenProvider;

	private final RedisUtil redisUtil;
	List<String> list = Arrays.asList("/swagger-ui/", "/swagger-resources/", "/v3/api-docs/", "/h2-console/",
		"/auth/token");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		if (list.contains(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}
		Instant beforeTime = Instant.now();
		String token = getTokenFromRequest(request);

		if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
			// Redis 에 해당 accessToken logout 여부 확인
			String isLogout = redisUtil.getAccessToken(token);
			if (ObjectUtils.isEmpty(isLogout)) {
				Authentication authentication = tokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				log.info("Save Authentication");
			}
		}

		filterChain.doFilter(request, response);
		log.info("url {} ,response 응답까지 시간  {}ms, {}s",
			request.getRequestURI(),
			Duration.between(beforeTime, Instant.now()).toMillis(),
			Duration.between(beforeTime, Instant.now()).toSeconds());
	}

	public String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}