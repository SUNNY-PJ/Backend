package com.sunny.backend.common;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse {
	@ApiModelProperty(value = "응답 코드 번호")
	private int status;

	public static class ErrorResponse extends CommonResponse {
		@ApiModelProperty(value = "응답 코드")
		private ErrorCode errorCode;

		public ErrorResponse(int status, ErrorCode errorCode) {
			super(status);
			this.errorCode = errorCode;
		}
	}

	public static class GeneralResponse extends CommonResponse {
		private String msg;

		public GeneralResponse(int status, String msg) {
			super(status);
			this.msg = msg;
		}
	}

	@Getter
	public static class SingleResponse<T> extends CommonResponse {
		private T data;
		@ApiModelProperty(value = "응답 메세지")
		private String msg;

		public SingleResponse(int status, T data, String msg) {
			super(status);
			this.data = data;
			this.msg = msg;
		}
	}

	@Getter
	public static class ListResponse<T> extends CommonResponse {
		private List<T> data;
		@ApiModelProperty(value = "응답 메세지")
		private String msg;

		public ListResponse(int status, List<T> data, String msg) {
			super(status);
			this.data = data;
			this.msg = msg;
		}
	}

}
