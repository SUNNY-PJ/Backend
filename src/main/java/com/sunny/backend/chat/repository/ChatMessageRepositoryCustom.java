package com.sunny.backend.chat.repository;

import com.sunny.backend.chat.dto.response.ChatMessageResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatMessageRepositoryCustom {
	Slice<ChatMessageResponse> getChatMessageList(Long chatRoomId, Pageable pageable);
}
