package com.sunny.backend.friends.dto.response;

import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.friends.domain.Status;

import lombok.Builder;

@Builder
public record FriendResponse(
	Long friendsId,
	Long competitionId,
	Long userFriendId,
	String nickname,
	String profile,
	Status friendStatus,
	Status competitionStatus,
	CompetitionStatus output
) {
	public static FriendResponse from(FriendResponseDto friendResponseDto) {
		return FriendResponse.builder()
			.friendsId(friendResponseDto.friendsId())
			.competitionId(friendResponseDto.competitionId())
			.userFriendId(friendResponseDto.friendsId())
			.nickname(friendResponseDto.nickname())
			.profile(friendResponseDto.profile())
			.friendStatus(friendResponseDto.friendStatus())
			.competitionStatus(friendResponseDto.competitionStatus())
			.output(isWinner(friendResponseDto.userId(), friendResponseDto.output()))
			.build();
	}

	public static CompetitionStatus isWinner(Long userId, Long output) {
		if (userId.equals(output)) {
			return CompetitionStatus.WIN;
		} else if (output.equals(-1L)) {
			return CompetitionStatus.DRAW;
		} else {
			return CompetitionStatus.LOSE;
		}
	}
}
