package com.sunny.backend.dto.request;

import lombok.Getter;

@Getter
public class PushRequestDto {
    private Long friendsId;
    private String title;
    private String body;
}
