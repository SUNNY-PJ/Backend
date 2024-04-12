package com.sunny.backend.friends.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendCompetitionStatus {
	NONE, REFUSE, SEND, RECEIVE, PROCEEDING, GIVE_UP, COMPLETE
}
