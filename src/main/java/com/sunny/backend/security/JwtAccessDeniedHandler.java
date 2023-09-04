package com.sunny.backend.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {

		// response.setCharacterEncoding("utf-8");
		// response.sendError(403, "권한이 없습니다.");
		log.error("권한없는 사용자의 접근");
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
