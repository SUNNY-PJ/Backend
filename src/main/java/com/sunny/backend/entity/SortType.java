package com.sunny.backend.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortType {
    VIEW("조회순"), LATEST("최신순");
    private final String sortType;
}
