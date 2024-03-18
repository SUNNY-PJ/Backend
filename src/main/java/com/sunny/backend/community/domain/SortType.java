package com.sunny.backend.community.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum SortType {
    VIEW("조회순"), LATEST("최신순");
    private final String value;
    SortType(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
    @JsonCreator
    public static SortType fromValue(String value) {
        for (SortType type : SortType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}