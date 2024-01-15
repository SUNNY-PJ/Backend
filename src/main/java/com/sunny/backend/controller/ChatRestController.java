package com.sunny.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.chat.ChatMessageResponse;
import com.sunny.backend.dto.request.chat.ChatRoomResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.ChatService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "10. Chat", description = "Chat API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRestController {

	private final ChatService chatService;

	@ApiOperation(tags = "10. Chat", value = "채팅 대화 조회")
	@GetMapping("/{chat_room_id}")
	public ResponseEntity<CommonResponse.ListResponse<ChatMessageResponse>> getChatMessageList(
		@PathVariable(name = "chat_room_id") Long chatRoomId) {
		return chatService.getChatMessageList(chatRoomId);
	}

	@ApiOperation(tags = "10. Chat", value = "채팅방 조회 조회")
	@GetMapping("/room")
	public ResponseEntity<CommonResponse.ListResponse<ChatRoomResponse>> getChatRoomList(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		return chatService.getChatRoomList(customUserPrincipal);
	}

	// @ApiOperation(tags = "9. Chat", value = "채팅방 생성")
	// @PostMapping("/{friend_id}")
	// public ResponseEntity<CommonResponse.GeneralResponse> createChatRoom(
	// 	@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "friend_id") Long friendId) {
	// 	return chatService.createChatRoom(customUserPrincipal, friendId);
	// }

	@ApiOperation(tags = "10. Chat", value = "채팅방 삭제")
	@DeleteMapping("/{chat_user_id}")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteChatRoom(@PathVariable(name = "chat_user_id") Long chatUserId) {
		return chatService.deleteChatRoom(chatUserId);
	}
}
