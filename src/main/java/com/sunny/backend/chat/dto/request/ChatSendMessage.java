package com.sunny.backend.chat.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public record ChatSendMessage (
	@PositiveOrZero(message = "채팅방 id는 양수 입니다.")
	Long chatRoomId,
	@Positive(message = "유저 id는 양수 입니다.")
	Long sendUserId,
	@NotEmpty(message = "메세지가 입력되지 않았습니다.")
	String message
){
}
