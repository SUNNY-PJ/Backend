package com.sunny.backend.chat.dto.response;

public interface ChatRoomRes {
	Long getChatRoomId();

	Long getUserFriendId();

	String getFriendName();

	String getFriendProfile();

	int getReadCnt();

	String getMessage();
}
