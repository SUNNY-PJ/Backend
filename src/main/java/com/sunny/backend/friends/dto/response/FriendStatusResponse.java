package com.sunny.backend.friends.dto.response;

import java.util.List;

import com.sunny.backend.friends.domain.Status;

public record FriendStatusResponse(
	List<FriendResponse> approveList,
	List<FriendResponse> waitList
) {
	public static FriendStatusResponse of(List<FriendResponseDto> approve, List<FriendResponseDto> wait) {
		List<FriendResponse> approveList = approve.stream()
			.filter(friend -> friend.friendStatus().equals(Status.APPROVE))
			.map(FriendResponse::from)
			.toList();

		List<FriendResponse> waitList = wait.stream()
			.filter(friend -> friend.friendStatus().equals(Status.WAIT))
			.map(FriendResponse::from)
			.toList();

		return new FriendStatusResponse(approveList, waitList);
	}
}
