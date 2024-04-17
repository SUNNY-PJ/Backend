package com.sunny.backend.friends.dto.response;

import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.domain.FriendStatus;

import lombok.Builder;

@Builder
public record FriendCompetitionResponse(
	Long friendId,
	Long userFriendId,
	Long competitionId,
	String nickname,
	String profile,
	FriendStatus friendStatus,
	FriendCompetitionStatus competitionStatus,
	CompetitionOutputStatus output
) {
	public static FriendCompetitionResponse fromCompetition(FriendCompetitionQuery friendCompetitionQuery) {
		FriendCompetitionStatus friendCompetitionStatus = friendCompetitionQuery.getFriendCompetitionStatus();
		return FriendCompetitionResponse.builder()
			.friendId(friendCompetitionQuery.getFriendId())
			.userFriendId(friendCompetitionQuery.getUserFriend())
			.competitionId(friendCompetitionQuery.getCompetitionId())
			.nickname(friendCompetitionQuery.getNickname())
			.profile(friendCompetitionQuery.getProfile())
			.friendStatus(friendCompetitionQuery.getFriendStatus())
			.competitionStatus(friendCompetitionQuery.getFriendCompetitionStatus())
			.output(friendCompetitionQuery.getCompetitionId() != null ?
				friendCompetitionQuery.getCompetitionOutputStatus() : CompetitionOutputStatus.NONE)
			.build();
	}

	public static FriendCompetitionResponse fromFriend(FriendCompetitionQuery friendCompetitionQuery) {
		FriendCompetitionStatus friendCompetitionStatus = friendCompetitionQuery.getFriendCompetitionStatus();
		return FriendCompetitionResponse.builder()
			.friendId(friendCompetitionQuery.getFriendId())
			.userFriendId(friendCompetitionQuery.getUserFriend())
			.competitionId(friendCompetitionQuery.getCompetitionId())
			.nickname(friendCompetitionQuery.getNickname())
			.profile(friendCompetitionQuery.getProfile())
			.friendStatus(friendCompetitionQuery.getFriendStatus())
			.competitionStatus(
				(friendCompetitionStatus == FriendCompetitionStatus.RECEIVE
					|| friendCompetitionStatus == FriendCompetitionStatus.RECEIVE)
					? friendCompetitionQuery.getFriendCompetitionStatus()
					: FriendCompetitionStatus.NONE)
			.output(friendCompetitionQuery.getCompetitionId() != null ?
				friendCompetitionQuery.getCompetitionOutputStatus() : CompetitionOutputStatus.NONE)
			.build();
	}
}
