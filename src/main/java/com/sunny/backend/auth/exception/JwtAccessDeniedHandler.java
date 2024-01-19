package com.sunny.backend.auth.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {

		log.error("권한없는 사용자의 접근");
		// 유효한 자격증명을 제공하지 않고 접근하려 할때 403
		final Map<String, Object> body = new HashMap<>();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		// 응답 객체 초기화
		body.put("status", HttpServletResponse.SC_FORBIDDEN);
		body.put("error", "Forbidden");
		body.put("message", accessDeniedException.getMessage());
		body.put("path", request.getServletPath());
		final ObjectMapper mapper = new ObjectMapper();
		// response 객체에 응답 객체를 넣어줌
		mapper.writeValue(response.getOutputStream(), body);
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
