package com.sunny.backend.repository.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.entity.chat.ChatUser;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
	Long countByChatRoom_Id(Long id);
	boolean existsByUsers_IdAndFriend_Id(Long userId, Long friendId);
	boolean existsByFriend_IdAndUsers_Id(Long friendId, Long userId);
	Optional<ChatUser> findByFriend_IdAndUsers_Id(Long friendId, Long userId);
	List<ChatUser> findByUsers_Id(Long userId);
}
