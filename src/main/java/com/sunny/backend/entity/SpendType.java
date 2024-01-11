package com.sunny.backend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpendType {
  FOOD("food"), SHELTER("shelter"), CLOTHING("clothing"), OTHERS("others");
  private final String type;
}
