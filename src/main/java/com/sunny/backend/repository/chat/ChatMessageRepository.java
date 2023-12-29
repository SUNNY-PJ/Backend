package com.sunny.backend.repository.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.entity.chat.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {
	List<ChatMessage> findByChatRoom_IdOrderByCreatedDate(Long chatRoomId);
}
