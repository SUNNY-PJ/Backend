package com.sunny.backend.common.response;

import java.util.List;

import com.sunny.backend.common.CommonErrorCode;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse {
	@ApiModelProperty(value = "응답 코드 번호")
	private int status;

	@Getter
	public static class ErrorResponse extends CommonResponse {
		@ApiModelProperty(value = "응답 코드")
		private CommonErrorCode commonErrorCode;

		public ErrorResponse(int status, CommonErrorCode commonErrorCode) {
			super(status);
			this.commonErrorCode = commonErrorCode;
		}
	}

	@Getter
	public static class GeneralResponse extends CommonResponse {
		private String msg;

		public GeneralResponse(int status, String msg) {
			super(status);
			this.msg = msg;
		}
	}

	public static class GeneralErrorResponse extends CommonResponse {
		private String msg;

		public GeneralErrorResponse(int status, String msg) {
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
