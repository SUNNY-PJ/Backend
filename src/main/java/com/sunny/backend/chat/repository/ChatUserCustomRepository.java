package com.sunny.backend.chat.repository;

import java.util.List;

import com.sunny.backend.chat.dto.response.ChatRoomResponse;

public interface ChatUserCustomRepository {
	List<ChatRoomResponse> getChatRoomResponseByUserId(Long userId);
}
