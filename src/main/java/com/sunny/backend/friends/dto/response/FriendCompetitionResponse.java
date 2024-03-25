package com.sunny.backend.friends.dto.response;

import com.sunny.backend.competition.domain.CompetitionOutputType;
import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;

import lombok.Builder;

@Builder
public record FriendCompetitionResponse(
	Long friendId,
	Long competitionId,
	Long userFriendId,
	String nickname,
	String profile,
	FriendStatus friendStatus,
	CompetitionStatus competitionStatus,
	CompetitionOutputType output
) {
	public static FriendCompetitionResponse from(Friend friend) {
		return FriendCompetitionResponse.builder()
			.friendId(friend.getId())
			.competitionId(friend.getCompetition().getId())
			.userFriendId(friend.getUserFriend().getId())
			.nickname(friend.getUserFriend().getNickname())
			.profile(friend.getUserFriend().getProfile())
			.friendStatus(friend.getStatus())
			.competitionStatus(friend.getCompetitionStatus())
			.output(friend.getCompetition().getOutput().isWinner(friend.getUsers().getId()))
			.build();
	}

}
