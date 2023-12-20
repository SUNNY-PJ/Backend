package com.sunny.backend.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.sunny.backend.dto.request.chat.ChatMessageDto;
import com.sunny.backend.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;

	/**
	 * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
	 */
	@MessageMapping("/{roomId}")
	@SendTo("/room/{roomId}")
	public void message(@DestinationVariable Long roomId, ChatMessageDto message) {
		// Websocket에 발행된 메시지를 redis로 발행한다(publish)
		chatService.createChatMsg(roomId, message);
	}
}