package com.sunny.backend.chat.dto.response;

import com.sunny.backend.chat.domain.ChatUser;

public record ChatRoomResponse(
	Long chatRoomId,
	Long userFriendId,
	String friendName,
	String friendProfile
	// int notReadCount,
	// String lastMessage,
	// LocalDateTime lastMessageTime,
) {
	// public ChatRoomResponse(ChatUser chatUser, ChatMessage chatMessage) {
	// 	this.chatRoomId = chatUser.getChatRoom().getId();
	// 	this.userFriendId = chatUser.getFriend().getId();
	// 	this.friendName = chatUser.getFriend().getName();
	// 	this.friendProfile = chatUser.getFriend().getProfile();
	// 	this.notReadCount = chatMessage.getReads().stream()
	// 		.map(msg -> msg.)
	// 	this.lastMessage = lastMessage;
	// 	this.lastMessageTime = lastMessageTime;
	// }

	public static ChatRoomResponse from(ChatUser chatUser) {
		return new ChatRoomResponse(
			chatUser.getChatRoom().getId(),
			chatUser.getFriend().getId(),
			chatUser.getFriend().getName(),
			chatUser.getFriend().getProfile()
		);
	}
}
