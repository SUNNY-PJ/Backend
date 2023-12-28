package com.sunny.backend.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.RequiredArgsConstructor;

@ControllerAdvice //컨트롤러 전역에서 발생하는 커스텀 에러를 잡아줄 Handler
@RequiredArgsConstructor
public class CustomExceptionHandler {
	private final ResponseService responseService;

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e) {
		return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<CommonResponse.GeneralResponse> handleException(Exception e) {
		return responseService.getGeneralResponse(400, e.getMessage());
	}
}
