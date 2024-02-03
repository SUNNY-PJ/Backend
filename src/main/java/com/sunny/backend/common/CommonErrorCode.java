package com.sunny.backend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import com.sunny.backend.common.exception.ErrorCode;

@AllArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {
    NO_USER_PERMISSION(HttpStatus.FORBIDDEN, "해당 글에 접근할 수 있는 권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_INVALID(HttpStatus.FORBIDDEN, "권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
