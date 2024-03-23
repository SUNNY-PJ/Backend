package com.sunny.backend.friends.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendStatus {
	NONE, PENDING, FRIEND
}
