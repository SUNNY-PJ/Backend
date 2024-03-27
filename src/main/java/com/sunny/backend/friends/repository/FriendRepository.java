package com.sunny.backend.friends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.user.domain.Users;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository {
	Optional<Friend> findByUsersAndUserFriend(Users users, Users userFriend);

	List<Friend> findByUsers(Users users);

	List<Friend> findByUsersAndCompetitionIsNotNullAndCompetition_Status(Users users, CompetitionStatus status);

	default Friend getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new IllegalArgumentException("친구가 존재하지 않습니다."));
	}
}
