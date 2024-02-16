package com.sunny.backend.chat.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageResponse {
	private String createdDate;
	private List<MessageResponse> messageResponses;
}
