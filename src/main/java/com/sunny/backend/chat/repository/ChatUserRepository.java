package com.sunny.backend.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.chat.domain.ChatUser;
import com.sunny.backend.chat.exception.ChatErrorCode;
import com.sunny.backend.common.exception.CustomException;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long>, ChatUserCustomRepository {
	Long countByChatRoom_Id(Long id);

	Optional<ChatUser> findByFriend_IdAndUsers_Id(Long friendId, Long userId);

	List<ChatUser> findByUsers_Id(Long userId);

	default ChatUser getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(ChatErrorCode.CHAT_USER_NOT_FOUND));
	}
}
