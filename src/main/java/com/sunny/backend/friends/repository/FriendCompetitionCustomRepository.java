package com.sunny.backend.friends.repository;

import java.util.List;

import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.dto.response.FriendCompetitionDto;

public interface FriendCompetitionCustomRepository {
	List<FriendCompetition> getByFriendAndCompetition(Long friendId, Long competitionId);

	List<FriendCompetitionDto> getByFriendLeftJoinFriend(Long userId);

	List<FriendCompetition> getByUserId(Long userId);
}
