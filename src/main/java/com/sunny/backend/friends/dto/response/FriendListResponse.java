package com.sunny.backend.friends.dto.response;

import java.util.List;

import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;

public record FriendListResponse(
	List<FriendCompetitionResponse> competitions,
	List<FriendResponse> approveList,
	List<FriendResponse> waitList
) {
	public static FriendListResponse of(List<Friend> friends) {
		List<FriendCompetitionResponse> competitions = friends.stream()
			.filter(Friend::isCompetition)
			.map(FriendCompetitionResponse::from)
			.toList();

		List<FriendResponse> approveList = friends.stream()
			.filter(friend -> !friend.isCompetition())
			.filter(Friend::isFriend)
			.map(FriendResponse::from)
			.toList();

		List<FriendResponse> waitList = friends.stream()
			.filter(friend -> !friend.isCompetition())
			.filter(Friend::isFriendPending)
			.map(FriendResponse::from)
			.toList();

		return new FriendListResponse(competitions, approveList, waitList);
	}

}
