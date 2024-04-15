package com.sunny.backend.notification.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum NotifiacationSubType {
	REGISTER("등록"), APPROVE("승인"), REFUSE("거절"),
	APPLY("신청"), GIVE_UP("포기"), WIN("승리"), DRAW("무승부"), LOSE("패배");

	private final String value;

	NotifiacationSubType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static NotifiacationSubType fromValue(String value) {
		for (NotifiacationSubType type : NotifiacationSubType.values()) {
			if (type.getValue().equals(value)) {
				return type;
			}
		}
		return null;
	}

}
