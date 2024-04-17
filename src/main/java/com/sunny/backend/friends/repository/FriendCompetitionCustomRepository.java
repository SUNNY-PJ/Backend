package com.sunny.backend.friends.repository;

import java.util.List;

import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.dto.response.FriendCompetitionResponse;

public interface FriendCompetitionCustomRepository {
	List<FriendCompetition> getByFriendAndCompetition(Long friendId, Long competitionId);

	List<FriendCompetitionResponse> getByFriendLeftJoinFriend(Long userId);

	List<FriendCompetition> getByUserId(Long userId);

	List<FriendCompetition> getByUserOrUserFriendByUserId(Long userId);

	List<FriendCompetition> getByUserOrUserFriend(Long userId, Long userFriendId);
}
