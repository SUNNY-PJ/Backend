package com.sunny.backend.community.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
	@JsonProperty("자유")
	FREE("자유"),
	@JsonProperty("꿀팁")
	TIP("꿀팁");
	private final String status;

}
