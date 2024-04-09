package com.sunny.backend.friends.repository;

import java.util.List;

import com.sunny.backend.competition.dto.response.CompetitionResultDto;

public interface FriendCustomRepository {
	void updateCompetitionToNull(Long competitionId);

	List<CompetitionResultDto> getCompetitionResult();
}
