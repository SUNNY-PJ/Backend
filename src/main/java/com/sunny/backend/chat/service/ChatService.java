package com.sunny.backend.chat.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.sunny.backend.chat.dto.response.ChatMessageResponse;
import com.sunny.backend.chat.dto.response.ChatRoomResponse;
import com.sunny.backend.chat.domain.ChatUser;
import com.sunny.backend.chat.repository.ChatMessageRepository;
import com.sunny.backend.chat.repository.ChatRoomRepository;
import com.sunny.backend.chat.repository.ChatUserRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatRoomRepository chatRoomRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatMessageRepository chatMessageRepository;

	public Slice<ChatMessageResponse> getChatMessageList(Long chatRoomId, Pageable pageable) {
		return chatMessageRepository.getChatMessageList(chatRoomId, pageable);
	}

	public List<ChatRoomResponse> getChatRoomList(CustomUserPrincipal customUserPrincipal) {
		return chatUserRepository.findByUsers_Id(customUserPrincipal.getUsers().getId())
			.stream()
			.map(ChatRoomResponse::from)
			.toList();
	}

	@Transactional
	public void deleteChatRoom(Long chatUserId) {
		ChatUser chatUser = chatUserRepository.getById(chatUserId);

		// A, B 대화 중 A, B 둘 다 채팅방 나갈 경우 채팅방 삭제
		Long count = chatUserRepository.countByChatRoom_Id(chatUser.getChatRoom().getId());
		if(count==1) {
			chatRoomRepository.deleteById(chatUser.getChatRoom().getId());
		}
		chatUserRepository.deleteById(chatUserId);
	}

}
