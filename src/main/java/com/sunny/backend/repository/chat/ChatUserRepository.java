package com.sunny.backend.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.entity.chat.ChatUser;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
}
