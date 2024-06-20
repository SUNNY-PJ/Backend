package com.sunny.backend.community.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/** 게시판 정렬 type
 * 조회순 (VIEW_COUNT)
 * 최신순 (LATEST)
 * */

@Getter
public enum SortType {
	VIEW_COUNT("조회순"), LATEST("최신순");
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