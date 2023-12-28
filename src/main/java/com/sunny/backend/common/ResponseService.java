package com.sunny.backend.common;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseService extends RuntimeException {
	public CommonResponse.ErrorResponse getErrorResponse(int status, ErrorCode errorCode) {
		return new CommonResponse.ErrorResponse(status, errorCode);
	}
	public CommonResponse.GeneralErrorResponse getGeneralErrorResponse(int status, String msg) {
		return new CommonResponse.GeneralErrorResponse(status, msg);
	}
	public ResponseEntity<CommonResponse.GeneralResponse> getGeneralResponse(int status, String msg) {
		return ResponseEntity.ok().body(new CommonResponse.GeneralResponse(status, msg));
	}

	public <T> ResponseEntity<CommonResponse.SingleResponse<T>> getSingleResponse(int status, T data, String msg) {
		return ResponseEntity.ok().body(new CommonResponse.SingleResponse<>(status, data, msg));
	}

	public <T> ResponseEntity<CommonResponse.ListResponse<T>> getListResponse(int status, List<T> data, String msg) {
		return ResponseEntity.ok().body(new CommonResponse.ListResponse<>(status, data, msg));
	}

}
