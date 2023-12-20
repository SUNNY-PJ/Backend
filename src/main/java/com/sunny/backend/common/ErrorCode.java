package com.sunny.backend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum ErrorCode {
    COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 글을 찾을 수 없습니다."),

    CONSUMPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "지출 내역을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 댓글을 찾을 수 없습니다."),
    NicknameAlreadyInUse(HttpStatus.FORBIDDEN,"이미 사용중인 닉네임입니다."),
    //To do : 게시글 작성자만 수정/삭제가 가능합니다?
    NO_USER_PERMISSION(HttpStatus.FORBIDDEN,"해당 글에 접근할 수 있는 권한이 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
