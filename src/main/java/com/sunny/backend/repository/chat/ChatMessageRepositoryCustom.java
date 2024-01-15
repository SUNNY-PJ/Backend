package com.sunny.backend.repository.chat;

import java.util.List;

import com.sunny.backend.dto.request.chat.ChatMessageResponse;

public interface ChatMessageRepositoryCustom {
	List<ChatMessageResponse> getChatMessageList(Long chatRoomId);
}
