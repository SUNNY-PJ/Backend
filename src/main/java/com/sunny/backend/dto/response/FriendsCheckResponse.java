package com.sunny.backend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sunny.backend.entity.friends.ApproveType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FriendsCheckResponse {
	boolean check;
	ApproveType approveType;

	public FriendsCheckResponse(boolean check, ApproveType approveType) {
		this.check = check;
		this.approveType = approveType;
	}
}
