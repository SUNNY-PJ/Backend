package com.sunny.backend.community.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {

	FREE("free"),

	TIP("tip");
	private final String value;

	@JsonCreator
	public static BoardType parsing(String inputValue) {
		return Stream.of(BoardType.values())
				.filter(category -> category.toString().equalsIgnoreCase(inputValue))
				.findFirst()
				.orElse(null);
	}
}
