package com.sunny.backend.chat.dto.response;

import com.sunny.backend.chat.domain.ChatUser;

public record ChatRoomResponse (
	Long chatRoomId,
	Long userFriendId,
	String friendName
){
	public static ChatRoomResponse from(ChatUser chatUser) {
		return new ChatRoomResponse(
			chatUser.getChatRoom().getId(),
			chatUser.getFriend().getId(),
			chatUser.getFriend().getName()
		);
	}
}
