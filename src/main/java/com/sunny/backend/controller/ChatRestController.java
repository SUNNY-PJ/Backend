package com.sunny.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.ChatService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "9. Chat", description = "Chat API")
@RestController
@RequiredArgsConstructor
public class ChatRestController {

	private final ChatService chatService;

	@PostMapping("/{friendId}")
	public ResponseEntity<CommonResponse.GeneralResponse> createChatRoom(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long friendId) {
		return chatService.createChatRoom(customUserPrincipal, friendId);
	}

	@DeleteMapping("/{chatUserId}")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteChatRoom(@PathVariable Long chatUserId) {
		return chatService.deleteChatRoom(chatUserId);
	}
}
