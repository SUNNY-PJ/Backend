package com.sunny.backend.notification.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotificationPushRequest {
	private Long postAuthor;
	private String title;
	private String body;

	public NotificationPushRequest(Long postAuthor, String title, String body) {
		this.postAuthor = postAuthor;
		this.title = title;
		this.body = body;
	}
}

