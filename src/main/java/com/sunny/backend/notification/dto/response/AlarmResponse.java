package com.sunny.backend.notification.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.notification.domain.NotificationType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class AlarmResponse implements Serializable {
	@Serial
	private static final long serialVersionUID = 123456789L;

	private String title;
	private String content;
	private String name;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime date;
	private NotificationType type;
}
