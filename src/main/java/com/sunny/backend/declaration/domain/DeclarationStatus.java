package com.sunny.backend.declaration.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeclarationStatus {
	COMMUNITY("커뮤니티"), COMMENT("댓글");

	private final String status;
}
