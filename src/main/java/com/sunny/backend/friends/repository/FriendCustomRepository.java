package com.sunny.backend.friends.repository;

import java.util.List;

import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.competition.dto.response.CompetitionResultDto;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.dto.response.FriendDto;

public interface FriendCustomRepository {
	// List<Friend> findFriends(Long userId, FriendStatus friendStatus, CompetitionStatus competitionStatus);

	List<CompetitionResultDto> getCompetitionResult();
}
