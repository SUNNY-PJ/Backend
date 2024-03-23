package com.sunny.backend.friends.dto.response;

import java.util.List;

import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.friends.domain.Friend;

public class FriendCompetition {
	private final List<FriendCompetitionResponse> competitionResponses;

	private FriendCompetition(List<FriendCompetitionResponse> competitionResponses) {
		this.competitionResponses = competitionResponses;
	}

	public static FriendCompetition from(List<Friend> friends) {
		List<FriendCompetitionResponse> competitionResponses = friends.stream()
			.filter(FriendCompetition::isProceeding)
			.map(FriendCompetitionResponse::from)
			.toList();
		return new FriendCompetition(competitionResponses);
	}

	private static Boolean isProceeding(Friend friend) {
		return friend.getCompetitionStatus().equals(CompetitionStatus.PROCEEDING);
	}
}
