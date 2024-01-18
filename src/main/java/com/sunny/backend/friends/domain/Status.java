package com.sunny.backend.friends.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
	APPROVE("승인"), WAIT("대기");

	private final String status;
}
