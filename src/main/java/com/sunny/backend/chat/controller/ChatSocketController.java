package com.sunny.backend.chat.controller;

import javax.validation.Valid;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import com.sunny.backend.chat.dto.request.ChatSendMessage;
import com.sunny.backend.chat.service.ChatSocketService;
import com.sunny.backend.security.jwt.TokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatSocketController {
	private final ChatSocketService chatSocketService;
	private final TokenProvider tokenProvider;
	/**
	 * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
	 */
	@MessageMapping("/chat/message")
	public void message(@Valid ChatSendMessage message, @Header("Authorization") String Authorization) {
		tokenProvider.validateToken(Authorization);
		if (StringUtils.hasText(Authorization) && tokenProvider.validateToken(Authorization)) {
			String email = tokenProvider.getClaims(Authorization).get("email").toString();
			chatSocketService.createChatMsg(message, email);
		}
	}
}