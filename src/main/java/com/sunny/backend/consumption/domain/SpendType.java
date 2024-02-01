package com.sunny.backend.consumption.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum SpendType {
  FOOD("식생활"), SHELTER("주거"), CLOTHING("의류"), OTHERS("기타");
  private final String value;
  SpendType(String value) {
    this.value = value;
  }
  @JsonValue
  public String getValue() {
    return value;
  }
  @JsonCreator
  public static SpendType fromValue(String value) {
    for (SpendType type : SpendType.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    return null;
  }
}