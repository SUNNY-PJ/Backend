package com.sunny.backend.friends.dto.response;

import java.util.List;

import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.domain.FriendStatus;

public record FriendListResponse(
	List<FriendCompetitionResponse> competitions,
	List<FriendCompetitionResponse> approveList,
	List<FriendResponse> waitList
) {
	public static FriendListResponse of(List<FriendCompetitionDto> friendCompetitions) {
		List<FriendCompetitionResponse> competitions = friendCompetitions.stream()
			.filter(friendCompetition -> friendCompetition.getCompetitionId() != null)
			.filter(friendCompetition -> friendCompetition.getFriendCompetitionStatus()
				== FriendCompetitionStatus.PROCEEDING)
			.map(FriendCompetitionResponse::from)
			.toList();

		List<FriendCompetitionResponse> approveList = friendCompetitions.stream()
			.filter(friendCompetition ->
				(friendCompetition.getCompetitionId() == null
					&& friendCompetition.getFriendStatus() == FriendStatus.FRIEND) || (
					friendCompetition.getCompetitionId() != null
						&& (friendCompetition.getFriendCompetitionStatus() == FriendCompetitionStatus.SEND
						|| friendCompetition.getFriendCompetitionStatus() == FriendCompetitionStatus.RECEIVE))
			)
			.map(FriendCompetitionResponse::from)
			.toList();

		List<FriendResponse> waitList = friendCompetitions.stream()
			.filter(friendCompetition -> friendCompetition.getCompetitionId() == null)
			.filter(
				friendCompetition -> friendCompetition.getFriendStatus() == FriendStatus.RECEIVE)
			.map(FriendResponse::from)
			.toList();

		return new FriendListResponse(competitions, approveList, waitList);
	}

}
