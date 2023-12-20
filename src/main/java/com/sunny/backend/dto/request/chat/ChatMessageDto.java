package com.sunny.backend.dto.request.chat;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatMessageDto {
	private Long chatRoomId;
	private Long userId;
	private String message;
}
