package com.sunny.backend.community.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum BoardType {
	TIP("절약 꿀팁"), FREE("자유 게시판");

	private final String value;

	BoardType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static BoardType fromValue(String value) {
		for (BoardType type : BoardType.values()) {
			if (type.getValue().equals(value)) {
				return type;
			}
		}
		return null;
	}
}