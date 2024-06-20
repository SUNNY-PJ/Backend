package com.sunny.backend.community.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/** 게시판 Type
 * 자유 게시판 (FREE)
 * 절약 꿀팁 (SAVING_TIPS)
 * */

@Getter
public enum BoardType {
	SAVING_TIPS("절약 꿀팁"), FREE("자유 게시판");
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