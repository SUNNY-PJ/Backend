package com.sunny.backend.chat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sunny.backend.common.BaseTime;
import com.sunny.backend.user.domain.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatUser extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

	@ManyToOne
	@JoinColumn(name = "friends_id")
	private Users friend;

	@ManyToOne
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	public ChatUser(Users users, Users friend, ChatRoom chatRoom) {
		this.users = users;
		this.friend = friend;
		this.chatRoom = chatRoom;
	}
}
