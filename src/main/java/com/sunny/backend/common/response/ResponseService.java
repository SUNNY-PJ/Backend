package com.sunny.backend.common.response;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonErrorCode;

@Service
public class ResponseService extends RuntimeException {
	public CommonResponse.ErrorResponse getErrorResponse(int status, CommonErrorCode commonErrorCode) {
		return new CommonResponse.ErrorResponse(status, commonErrorCode);
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
