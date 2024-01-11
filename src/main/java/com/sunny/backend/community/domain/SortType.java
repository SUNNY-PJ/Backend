package com.sunny.backend.community.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    VIEW("view"),

    LATEST("latest");
    private final String value;

    @JsonCreator
    public static SortType parsing(String inputValue) {
        return Stream.of(SortType.values())
            .filter(category -> category.toString().equalsIgnoreCase(inputValue))
            .findFirst()
            .orElse(null);
    }
}
