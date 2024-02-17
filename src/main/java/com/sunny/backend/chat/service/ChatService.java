package com.sunny.backend.chat.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.chat.domain.ChatUser;
import com.sunny.backend.chat.dto.response.ChatMessageResponse;
import com.sunny.backend.chat.dto.response.ChatRoomRes;
import com.sunny.backend.chat.dto.response.MessageResponse;
import com.sunny.backend.chat.repository.ChatMessageRepository;
import com.sunny.backend.chat.repository.ChatRoomRepository;
import com.sunny.backend.chat.repository.ChatUserRepository;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatRoomRepository chatRoomRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatMessageRepository chatMessageRepository;

	@Transactional
	public List<ChatMessageResponse> getChatMessageList(CustomUserPrincipal customUserPrincipal, Long chatRoomId,
		Integer size, Long chatMessageId) {
		Users users = customUserPrincipal.getUsers();
		List<ChatMessageResponse> responses =
			chatMessageRepository.getChatMessageList(chatRoomId, size, chatMessageId);

		for (ChatMessageResponse chatMessageResponse : responses) {
			for (MessageResponse messageResponse : chatMessageResponse.getMessageResponses()) {
				if (messageResponse.getRead() == 1 && !messageResponse.getUserId().equals(users.getId())) {
					messageResponse.setRead(0);
					chatMessageRepository.readMessage(messageResponse.getId());
				}
			}
		}

		return responses;
	}

	public List<ChatRoomRes> getChatRoomList(CustomUserPrincipal customUserPrincipal) {
		return chatMessageRepository.findByChatRoomResponse(customUserPrincipal.getUsers().getId());
		// return chatUserRepository.findByUsers_Id(customUserPrincipal.getUsers().getId())
		// 	.stream()
		// 	.map(ChatRoomResponse::from)
		// 	.toList();
	}

	@Transactional
	public void deleteChatRoom(Long chatUserId) {
		ChatUser chatUser = chatUserRepository.getById(chatUserId);

		// A, B 대화 중 A, B 둘 다 채팅방 나갈 경우 채팅방 삭제
		Long count = chatUserRepository.countByChatRoom_Id(chatUser.getChatRoom().getId());
		if (count == 1) {
			chatRoomRepository.deleteById(chatUser.getChatRoom().getId());
		}
		chatUserRepository.deleteById(chatUserId);
	}

}
