package com.sunny.backend.friends.dto.response;

import java.util.List;

import com.sunny.backend.friends.domain.Status;

public record FriendStatusResponse (
	List<FriendResponse> approveList,
	List<FriendResponse> waitList
){
	public static FriendStatusResponse of(List<FriendResponse> approve, List<FriendResponse> wait) {
		List<FriendResponse> approveList = approve.stream()
			.filter(friend -> friend.friendStatus().equals(Status.APPROVE))
			.toList();

		List<FriendResponse> waitList = wait.stream()
			.filter(friend -> friend.friendStatus().equals(Status.WAIT))
			.toList();

		return new FriendStatusResponse(approveList, waitList);
	}
}
