package com.sunny.backend.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomResponse {
	private Long chatRoomId;
	private Long userFriendId;
	private String friendName;
	private String friendProfile;
	private int readCnt;
	private String message;
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
	// private Date time;
}