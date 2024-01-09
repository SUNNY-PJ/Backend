package com.sunny.backend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommonCustomException extends RuntimeException{
    CommonErrorCode commonErrorCode; // 상태 정보 + 메시지 담긴 에러 코드
}
