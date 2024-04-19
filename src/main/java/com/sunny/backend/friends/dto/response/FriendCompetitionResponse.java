package com.sunny.backend.friends.dto.response;

import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.friends.domain.Friend;
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

		// friendCompetitionStatus가 RECEIVE 또는 SEND인 경우 유지, 그 외의 경우 NONE으로 설정
		friendCompetitionStatus = (friendCompetitionStatus == FriendCompetitionStatus.RECEIVE ||
			friendCompetitionStatus == FriendCompetitionStatus.SEND)
			? friendCompetitionStatus
			: FriendCompetitionStatus.NONE;

		CompetitionOutputStatus output = (friendCompetitionQuery.getCompetitionId() != null || friendCompetitionStatus == FriendCompetitionStatus.NONE)
			? CompetitionOutputStatus.NONE
			: friendCompetitionQuery.getCompetitionOutputStatus();

		return FriendCompetitionResponse.builder()
			.friendId(friendCompetitionQuery.getFriendId())
			.userFriendId(friendCompetitionQuery.getUserFriend())
			.competitionId(friendCompetitionQuery.getCompetitionId())
			.nickname(friendCompetitionQuery.getNickname())
			.profile(friendCompetitionQuery.getProfile())
			.friendStatus(friendCompetitionQuery.getFriendStatus())
			.competitionStatus(friendCompetitionStatus)
			.output(output)
			.build();
	}

	public static FriendCompetitionResponse from(Friend friend) {
		return FriendCompetitionResponse.builder()
			.friendId(friend.getId())
			.userFriendId(friend.getUserFriend().getId())
			.nickname(friend.getUserFriend().getNickname())
			.profile(friend.getUserFriend().getProfile())
			.friendStatus(FriendStatus.FRIEND)
			.competitionStatus(FriendCompetitionStatus.NONE)
			.output(CompetitionOutputStatus.NONE)
			.build();
	}
}
