package com.sunny.backend.competition.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionOutputStatus {
	NONE, WIN, LOSE, DRAW;
}
