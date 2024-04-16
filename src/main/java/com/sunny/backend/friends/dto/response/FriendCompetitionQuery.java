package com.sunny.backend.friends.dto.response;

import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.domain.FriendStatus;

public interface FriendCompetitionQuery {
	Long getFriendId();
	Long getUserFriend();
	Long getCompetitionId();
	String getNickname();
	String getProfile();
	FriendStatus getFriendStatus();
	FriendCompetitionStatus getFriendCompetitionStatus();
	CompetitionOutputStatus getOutput();
}
