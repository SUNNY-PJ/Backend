package com.sunny.backend.friends.dto.response;

import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.domain.FriendStatus;

import lombok.Builder;

@Builder
public record FriendCompetitionResponse(
	Long friendId,
	Long userFriendId,
	String nickname,
	String profile,
	FriendStatus friendStatus,
	FriendCompetitionStatus competitionStatus,
	CompetitionOutputStatus output
) {
	public static FriendCompetitionResponse from(FriendCompetitionDto friendCompetitionDto) {
		return FriendCompetitionResponse.builder()
			.friendId(friendCompetitionDto.getFriendId())
			.userFriendId(friendCompetitionDto.getUserFriendId())
			.nickname(friendCompetitionDto.getNickname())
			.profile(friendCompetitionDto.getProfile())
			.friendStatus(friendCompetitionDto.getFriendStatus())
			.competitionStatus(
				friendCompetitionDto.getCompetitionId() != null ? friendCompetitionDto.getFriendCompetitionStatus()
					: FriendCompetitionStatus.NONE)
			.output(friendCompetitionDto.getCompetitionId() != null ?
				friendCompetitionDto.getCompetitionOutputStatus() : CompetitionOutputStatus.NONE)
			.build();
	}

}
