package com.sunny.backend.chat.repository;

import java.util.List;

import com.sunny.backend.chat.dto.response.ChatMessageResponse;

public interface ChatMessageRepositoryCustom {
	List<ChatMessageResponse> getChatMessageList(Long chatRoomId, Integer size, Long chatMessageId);
}
