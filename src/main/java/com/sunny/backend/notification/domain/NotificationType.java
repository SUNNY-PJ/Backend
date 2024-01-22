package com.sunny.backend.notification.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum NotificationType {
	COMMENT("댓글"), FRIEND("친구"), COMPETITION("대결");

	private final String value;

	NotificationType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static NotificationType fromValue(String value) {
		for (NotificationType type : NotificationType.values()) {
			if (type.getValue().equals(value)) {
				return type;
			}
		}
		return null;
	}
}
