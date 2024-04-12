package com.sunny.backend.friends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.user.domain.Users;

public interface FriendCompetitionRepository
	extends JpaRepository<FriendCompetition, Long>, FriendCompetitionCustomRepository {
	void deleteAllByFriend(Friend friend);

	Optional<FriendCompetition> findByFriendAndCompetition(Friend friend, Competition competition);

	Optional<FriendCompetition> findByFriendOrderByCreatedDateDesc(Friend friend);

	List<FriendCompetition> findByFriend_Users(Users users);
}
