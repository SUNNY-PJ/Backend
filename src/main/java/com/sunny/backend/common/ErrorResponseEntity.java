package com.sunny.backend.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(CommonErrorCode e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .message(e.getMessage())
                        .build()
                );
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e){
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(ErrorResponseEntity.builder()
                .status(e.getHttpStatus().value())
                .message(e.getMessage())
                .build()
            );
    }
}