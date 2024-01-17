package com.sunny.backend.competition.repository;

import java.util.Optional;

import com.sunny.backend.competition.domain.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

	// Optional<Competition> findByUsers_IdAndUserFriend_Id(Long userId, Long userFriendId);

	default Competition getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
	}
}
