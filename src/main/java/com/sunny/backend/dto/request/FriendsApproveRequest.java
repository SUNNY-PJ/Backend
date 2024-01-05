package com.sunny.backend.dto.request;

import javax.validation.constraints.NotNull;

public record FriendsApproveRequest (
	@NotNull(message = "승인에 대한 true/false가 입력되지 않았습니다.")
	Boolean approve
) {
}
