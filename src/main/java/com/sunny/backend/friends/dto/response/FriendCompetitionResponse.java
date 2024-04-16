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
	// public static FriendCompetitionResponse from(FriendCompetitionDto friendCompetitionDto) {
	// 	return FriendCompetitionResponse.builder()
	// 		.friendId(friendCompetitionDto.getFriendId())
	// 		.userFriendId(friendCompetitionDto.getUserFriendId())
	// 		.competitionId(friendCompetitionDto.getCompetitionId())
	// 		.nickname(friendCompetitionDto.getNickname())
	// 		.profile(friendCompetitionDto.getProfile())
	// 		.friendStatus(friendCompetitionDto.getFriendStatus())
	// 		.competitionStatus(
	// 			friendCompetitionDto.getCompetitionId() != null ? friendCompetitionDto.getFriendCompetitionStatus()
	// 				: FriendCompetitionStatus.NONE)
	// 		.output(friendCompetitionDto.getCompetitionId() != null ?
	// 			friendCompetitionDto.getCompetitionOutputStatus() : CompetitionOutputStatus.NONE)
	// 		.build();
	// }

	public static FriendCompetitionResponse from(FriendCompetitionQuery friendCompetitionQuery) {
		return FriendCompetitionResponse.builder()
			.friendId(friendCompetitionQuery.getFriendId())
			.userFriendId(friendCompetitionQuery.getUserFriend())
			.competitionId(friendCompetitionQuery.getCompetitionId())
			.nickname(friendCompetitionQuery.getNickname())
			.profile(friendCompetitionQuery.getProfile())
			.friendStatus(friendCompetitionQuery.getFriendStatus())
			.competitionStatus(
				friendCompetitionQuery.getCompetitionId() != null ? friendCompetitionQuery.getFriendCompetitionStatus()
					: FriendCompetitionStatus.NONE)
			.output(friendCompetitionQuery.getCompetitionId() != null ?
				friendCompetitionQuery.getOutput() : CompetitionOutputStatus.NONE)
			.build();
	}
}
