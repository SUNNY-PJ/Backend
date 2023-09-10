package com.sunny.backend.common;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ResponseService extends RuntimeException{
	public CommonResponse.ErrorResponse getErrorResponse(int status, ErrorCode errorCode) {
		return new CommonResponse.ErrorResponse(true,status,errorCode);
	}

	public CommonResponse.GeneralResponse getGeneralResponse(int status, String msg) {
		return new CommonResponse.GeneralResponse(true, status, msg);
	}

	public <T> CommonResponse.SingleResponse<T> getSingleResponse(int status, T data, String msg) {
		return new CommonResponse.SingleResponse<>(true, status, data, msg);
	}

	public <T> CommonResponse.ListResponse<T> getListResponse(int status, List<T> data, String msg) {
		return new CommonResponse.ListResponse<>(true, status, data, msg);
	}


}
