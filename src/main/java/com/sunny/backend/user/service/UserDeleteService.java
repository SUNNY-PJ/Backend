package com.sunny.backend.user.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.repository.FriendCompetitionRepository;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.repository.CompetitionNotificationRepository;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeleteService {

	private final FriendRepository friendRepository;
	private final CompetitionNotificationRepository competitionNotificationRepository;
	private final FriendCompetitionRepository friendCompetitionRepository;
	private final CompetitionRepository competitionRepository;
	private final FriendsNotificationRepository friendsNotificationRepository;

	public void deleteFriendRelationships(List<FriendCompetition> friendCompetitions) {
		for (FriendCompetition friendCompetition : friendCompetitions) {
			competitionNotificationRepository.deleteAllByFriendCompetition(friendCompetition);
			friendCompetitionRepository.deleteById(friendCompetition.getId());
		}
		Set<Long> competitionIds = friendCompetitions.stream()
			.map(friendCompetition -> friendCompetition.getCompetition().getId())
			.collect(Collectors.toSet());
		if (!competitionIds.isEmpty()) {
			competitionRepository.deleteAllById(competitionIds);
		}
	}

	public void deleteFriendRelationshipsByUser(List<FriendCompetition> friendCompetitions, Users users, Users usersFriend) {
		deleteFriendRelationships(friendCompetitions);

		friendsNotificationRepository.deleteByUsersAndFriend(users, usersFriend);
		friendsNotificationRepository.deleteByUsersAndFriend(usersFriend, users);
		friendRepository.deleteByUsersAndUserFriend(users, usersFriend);
		friendRepository.deleteByUsersAndUserFriend(usersFriend, users);
	}
}
