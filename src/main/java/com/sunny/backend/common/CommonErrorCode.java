package com.sunny.backend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum CommonErrorCode {
    COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 글을 찾을 수 없습니다."),

    CONSUMPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "지출 내역을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    NicknameAlreadyInUse(HttpStatus.FORBIDDEN, "이미 사용중인 닉네임입니다."),
    NOTIFICATIONS_NOT_SENT(HttpStatus.NOT_FOUND, "해당 사용자에게 알림을 보낼 수 없습니다."),
    REPLYING_NOT_ALLOWED(HttpStatus.FORBIDDEN, "대댓글의 댓글을 남길 수 없습니다."),
    NO_USER_PERMISSION(HttpStatus.FORBIDDEN, "해당 글에 접근할 수 있는 권한이 없습니다."),
    INVALID_FUTURE_DATE(HttpStatus.FORBIDDEN, "미래 날짜는 사용할 수 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_INVALID(HttpStatus.FORBIDDEN, "권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
