package com.sunny.backend.entity.friends;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApproveType {
	APPROVE("승인"), REFUSE("거절"), WAIT("대기");

	private final String status;
}