package com.sunny.backend.competition.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionStatus {
	NONE, PENDING, PROCEEDING, GIVE_UP, COMPLETE;
}
