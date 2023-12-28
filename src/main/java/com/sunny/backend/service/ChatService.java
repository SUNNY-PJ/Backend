package com.sunny.backend.service;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.chat.ChatMessageDto;
import com.sunny.backend.entity.chat.ChatMessage;
import com.sunny.backend.entity.chat.ChatRoom;
import com.sunny.backend.entity.chat.ChatUser;
import com.sunny.backend.repository.chat.ChatMessageRepository;
import com.sunny.backend.repository.chat.ChatRoomRepository;
import com.sunny.backend.repository.chat.ChatUserRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ResponseService responseService;
	private final UserRepository userRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatMessageRepository chatMessageRepository;

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> createChatRoom(CustomUserPrincipal customUserPrincipal,
		Long friendId) {
		ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(2));
		Users users = customUserPrincipal.getUsers();
		Users friend = userRepository.findById(friendId)
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + friendId));
		chatUserRepository.save(new ChatUser(users, friend, chatRoom));
		chatUserRepository.save(new ChatUser(friend, users, chatRoom));
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "채팅방 생성 성공");
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteChatRoom(Long chatUserId) {
		chatUserRepository.deleteById(chatUserId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "채팅방 삭제 성공");
	}

	public void createChatMsg(Long roomId, ChatMessageDto chat) {
		ChatRoom chatRoom = chatRoomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + roomId));
		Users users = userRepository.findById(chat.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + chat.getUserId()));
		ChatMessage chatMessage = new ChatMessage(chat.getMessage(), users, chatRoom);
		chatMessageRepository.save(chatMessage);
	}
}
