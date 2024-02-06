package com.sunny.backend.common.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import com.sunny.backend.common.CommonErrorCode;

@Data
@Builder
public class ErrorResponseHandler {
    private int status;
    private String message;

    public static ResponseEntity<ErrorResponseHandler> toResponseEntity(ErrorCode e){
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(ErrorResponseHandler.builder()
                .status(e.getHttpStatus().value())
                .message(e.getMessage())
                .build()
            );
    }
}