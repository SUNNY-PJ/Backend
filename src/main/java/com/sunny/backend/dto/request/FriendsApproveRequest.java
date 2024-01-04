package com.sunny.backend.dto.request;

public record FriendsApproveRequest (
	Long friendsSn,
	boolean approve
) {
}
