package com.sunny.backend.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.chat.domain.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	default ChatRoom getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
	}
}
