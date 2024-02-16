package com.sunny.backend.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunny.backend.chat.domain.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {
	@Modifying
	@Query(value = "UPDATE chat_message cm SET cm.read = 0 where cm.id = :id ", nativeQuery = true)
	void readMessage(@Param(value = "id") Long id);
}
