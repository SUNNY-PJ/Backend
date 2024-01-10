package com.sunny.backend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpendType {
  FOOD("식생활"), SHELTER("주거"), CLOTHING("의류"), OTHERS("기타");
  private final String spendType;
}
