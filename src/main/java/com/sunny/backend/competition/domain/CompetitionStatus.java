package com.sunny.backend.competition.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionStatus {
	NONE, SEND, RECEIVE, PROCEEDING, GIVE_UP, COMPLETE;
}
