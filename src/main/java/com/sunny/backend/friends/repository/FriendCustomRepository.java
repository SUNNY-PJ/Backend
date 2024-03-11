package com.sunny.backend.friends.repository;

import java.util.List;

import com.sunny.backend.competition.dto.response.CompetitionResultDto;
import com.sunny.backend.friends.dto.response.FriendResponseDto;

public interface FriendCustomRepository {
	List<FriendResponseDto> getFriendResponse(Long userId);

	List<CompetitionResultDto> getCompetitionResult();
}
