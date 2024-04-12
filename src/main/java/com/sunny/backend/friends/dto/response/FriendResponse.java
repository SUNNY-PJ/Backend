package com.sunny.backend.friends.dto.response;

import com.sunny.backend.friends.domain.FriendStatus;

import lombok.Builder;

@Builder
public record FriendResponse(
	Long friendId,
	Long userFriendId,
	String nickname,
	String profile,
	FriendStatus friendStatus
) {
	public static FriendResponse from(FriendCompetitionDto friendCompetitionDto) {
		return FriendResponse.builder()
			.friendId(friendCompetitionDto.getFriendId())
			.userFriendId(friendCompetitionDto.getUserFriendId())
			.nickname(friendCompetitionDto.getNickname())
			.profile(friendCompetitionDto.getProfile())
			.friendStatus(friendCompetitionDto.getFriendStatus())
			.build();
	}

}
