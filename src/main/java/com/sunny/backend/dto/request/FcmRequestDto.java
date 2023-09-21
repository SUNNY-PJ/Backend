package com.sunny.backend.dto.request;

import lombok.Getter;

@Getter
public class FcmRequestDto {
    private String targetToken;
    private String title;
    private String body;

}
