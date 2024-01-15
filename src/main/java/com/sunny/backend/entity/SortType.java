package com.sunny.backend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    VIEW("view"), LATEST("latest");
    private final String sortType;
}
