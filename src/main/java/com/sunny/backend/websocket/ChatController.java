package com.sunny.backend.websocket;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import com.sunny.backend.dto.request.chat.ChatSendMessage;
import com.sunny.backend.security.jwt.TokenProvider;
import com.sunny.backend.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	private final TokenProvider tokenProvider;
	/**
	 * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
	 */
	@MessageMapping("/chat/message")
	public void message(ChatSendMessage message, @Header("Authorization") String Authorization) {
		tokenProvider.validateToken(Authorization);
		if (StringUtils.hasText(Authorization) && tokenProvider.validateToken(Authorization)) {
			String email = tokenProvider.getClaims(Authorization).get("email").toString();
			chatService.createChatMsg(message, email);
		}
	}
}