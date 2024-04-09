package com.sunny.backend.community.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum SortType {
	VIEW("조회순"), LATEST("최신순");
	private final String value;

	SortType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static SortType fromValue(String value) {
		for (SortType type : SortType.values()) {
			if (type.getValue().equals(value)) {
				return type;
			}
		}
		return null;
	}
}