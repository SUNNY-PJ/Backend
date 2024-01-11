package com.sunny.backend.consumption.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpendType {
  FOOD("food"), SHELTER("shelter"), CLOTHING("clothing"), OTHERS("others");
  private final String value;

  @JsonCreator
  public static SpendType parsing(String inputValue) {
    return Stream.of(SpendType.values())
        .filter(category -> category.toString().equalsIgnoreCase(inputValue))
        .findFirst()
        .orElse(null);
  }
}