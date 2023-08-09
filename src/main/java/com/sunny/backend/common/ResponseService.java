package com.sunny.backend.common;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ResponseService {
	public CommonResponse.GeneralResponse getGeneralResponse(int code, String msg) {
		return new CommonResponse.GeneralResponse(true, code, msg);
	}

	public <T> CommonResponse.SingleResponse<T> getSingleResponse(int code, T data) {
		return new CommonResponse.SingleResponse<>(true, code, data);
	}

	public <T> CommonResponse.ListResponse<T> getListResponse(int code, List<T> data) {
		return new CommonResponse.ListResponse<>(true, code, data);
	}

}
