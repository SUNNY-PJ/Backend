package com.sunny.backend.chat.dto.response;

import java.util.List;

public record ChatMessageResponse(
	String createdDate,
	List<MessageResponse> messageResponses
) {
}
