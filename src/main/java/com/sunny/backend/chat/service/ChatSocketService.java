package com.sunny.backend.chat.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sunny.backend.chat.domain.ChatMessage;
import com.sunny.backend.chat.domain.ChatRoom;
import com.sunny.backend.chat.domain.ChatUser;
import com.sunny.backend.chat.dto.request.ChatSendMessage;
import com.sunny.backend.chat.repository.ChatMessageRepository;
import com.sunny.backend.chat.repository.ChatRoomRepository;
import com.sunny.backend.chat.repository.ChatUserRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatSocketService {
	private final UserRepository userRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final SimpMessagingTemplate template;

	@Transactional
	public void createChatMsg(ChatSendMessage chatSendMessage, String email) {
		Users user = userRepository.getByEmail(email);
		Users userFriend = userRepository.getById(chatSendMessage.sendUserId());

		ChatRoom chatRoom;
		// 0인 경우 채팅방 생성
		if (isZeroChatRoomId(chatSendMessage.chatRoomId())) {
			Optional<ChatUser> chatUserOptional = chatUserRepository
				.findByFriend_IdAndUsers_Id(chatSendMessage.sendUserId(), user.getId());
			if (chatUserOptional.isPresent()) {
				chatRoom = chatUserOptional.get().getChatRoom(); // 친구의 채팅방이 있는 경우 해당 채팅방 사용
			} else {
				chatRoom = chatRoomRepository.save(new ChatRoom(2));
				chatUserRepository.save(new ChatUser(userFriend, user, chatRoom)); // 친구 채팅방
			}
			chatUserRepository.save(new ChatUser(user, userFriend, chatRoom)); // 본인 채팅방
		} else {
			chatRoom = chatRoomRepository.getById(chatSendMessage.chatRoomId());
		}

		ChatMessage chatMessage = new ChatMessage(chatSendMessage.message(), user, chatRoom);
		chatMessageRepository.save(chatMessage);
		template.convertAndSend("/sub/room/" + chatRoom.getId(), chatSendMessage.message());
	}

	private boolean isZeroChatRoomId(Long chatRoomId) {
		return chatRoomId == 0;
	}
}
