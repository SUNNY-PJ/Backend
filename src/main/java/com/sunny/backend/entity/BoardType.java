package com.sunny.backend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
	FREE("free"), TIP("tip");
	private final String boardType;

}
