package com.sunny.backend.chat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.chat.dto.response.ChatMessageResponse;
import com.sunny.backend.chat.dto.response.ChatRoomResponse;
import com.sunny.backend.chat.service.ChatService;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "8. Chat", description = "Chat API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
	private final ResponseService responseService;
	private final ChatService chatService;

	@ApiOperation(tags = "8. Chat", value = "채팅 대화 조회")
	@GetMapping("/{chatRoomId}")
	public ResponseEntity<List<ChatMessageResponse>> getChatMessageList(
		@AuthUser CustomUserPrincipal customUserPrincipal,
		@PathVariable(name = "chatRoomId") Long chatRoomId,
		@RequestParam(name = "size", defaultValue = "30") Integer size,
		@RequestParam(name = "chatMessageId", required = false) Long chatMessageId) {
		List<ChatMessageResponse> chatMessageResponses = chatService.getChatMessageList(customUserPrincipal,
			chatRoomId, size, chatMessageId);
		return ResponseEntity.ok().body(chatMessageResponses);
	}

	@ApiOperation(tags = "8. Chat", value = "채팅방 조회 조회")
	@GetMapping("/room")
	public ResponseEntity<CommonResponse.ListResponse<ChatRoomResponse>> getChatRoomList(
		@AuthUser CustomUserPrincipal customUserPrincipal) {
		List<ChatRoomResponse> chatRoomResponses = chatService.getChatRoomList(customUserPrincipal);
		return responseService.getListResponse(HttpStatus.OK.value(), chatRoomResponses, "채팅방 목록 조회");
	}

	@ApiOperation(tags = "8. Chat", value = "채팅방 삭제")
	@DeleteMapping("/{chatRoomId}")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteChatRoom(
		@PathVariable(name = "chatRoomId") Long chatUserId) {
		chatService.deleteChatRoom(chatUserId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "채팅방 삭제 성공");
	}
}
