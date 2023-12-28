package com.sunny.backend.entity.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.sunny.backend.entity.BaseTime;
import com.sunny.backend.user.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String message;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

	@OneToOne
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	public ChatMessage(String message, Users users, ChatRoom chatRoom) {
		this.message = message;
		this.users = users;
		this.chatRoom = chatRoom;
	}
}
