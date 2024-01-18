package com.sunny.backend.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;

import lombok.RequiredArgsConstructor;

@ControllerAdvice //컨트롤러 전역에서 발생하는 커스텀 에러를 잡아줄 Handler
@RequiredArgsConstructor
public class CustomExceptionHandler {
	private final ResponseService responseService;

	@ExceptionHandler(CommonCustomException.class)
	protected ResponseEntity<ErrorResponseHandler> handleCommonCustomException(CommonCustomException e) {
		return ErrorResponseHandler.toResponseEntity(e.getCommonErrorCode());
	}

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<ErrorResponseHandler> handleCustomException(CustomException e) {
		return ErrorResponseHandler.toResponseEntity(e.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<CommonResponse.GeneralResponse> handleException(Exception e) {
		return responseService.getGeneralResponse(400, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors()
			.forEach(c -> errors.put(((FieldError)c).getField(), c.getDefaultMessage()));
		return ResponseEntity.badRequest().body(errors);
	}
}
