package com.sunny.backend.competition.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionOutputType {
  WIN("승리"), LOSE("패배"),DRAW("무승부");

  private final String CompetitionStatus;
}
