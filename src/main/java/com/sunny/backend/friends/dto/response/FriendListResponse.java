package com.sunny.backend.friends.dto.response;

import java.util.List;

import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;

public record FriendListResponse(
	List<FriendCompetitionResponse> competitions,
	List<FriendCompetitionResponse> approveList,
	List<FriendResponse> waitList
) {
	public static FriendListResponse of(List<Friend> friends) {
		List<FriendCompetitionResponse> competitions = friends.stream()
			.filter(Friend::hasCompetition)
			.filter(friend -> friend.getCompetition().isEqualToCompetitionStatus(CompetitionStatus.PROCEEDING))
			.map(FriendCompetitionResponse::from)
			.toList();

		List<FriendCompetitionResponse> approveList = friends.stream()
			.filter(friend -> (!friend.hasCompetition() && friend.isEqualToFriendStatus(FriendStatus.FRIEND))
				|| (friend.hasCompetition() && !friend.getCompetition()
				.isEqualToCompetitionStatus(CompetitionStatus.PROCEEDING)))
			.map(FriendCompetitionResponse::from)
			.toList();

		List<FriendResponse> waitList = friends.stream()
			.filter(friend -> !friend.hasCompetition())
			.filter(friend -> friend.isEqualToFriendStatus(FriendStatus.RECEIVE))
			.map(FriendResponse::from)
			.toList();

		return new FriendListResponse(competitions, approveList, waitList);
	}

}
