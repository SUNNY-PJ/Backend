package com.sunny.backend.friends.repository;

import static com.sunny.backend.friends.domain.QFriendCompetition.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.competition.domain.QCompetition;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.domain.QFriend;
import com.sunny.backend.friends.domain.QFriendCompetition;
import com.sunny.backend.friends.dto.response.FriendCompetitionDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendCompetitionRepositoryImpl implements FriendCompetitionCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<FriendCompetition> getByFriendAndCompetition(Long friendId, Long competitionId) {
		return jpaQueryFactory.selectFrom(friendCompetition)
			.where(friendCompetition.friend.id.eq(friendId), eqCompetitionId(competitionId))
			.fetch();
	}

	@Override
	public List<FriendCompetitionDto> getByFriendLeftJoinFriend(Long userId) {
		QFriendCompetition friendCompetition = QFriendCompetition.friendCompetition;
		QCompetition competition = QCompetition.competition;
		QFriend friend = QFriend.friend;
		return jpaQueryFactory.select(
				Projections.constructor(FriendCompetitionDto.class, friend.id, friend.userFriend.id,
					friend.userFriend.nickname, friend.userFriend.profile, friend.status,
					friendCompetition.competition.id, friendCompetition.competition.message,
					friendCompetition.competition.startDate, friendCompetition.competition.endDate,
					friendCompetition.competition.price, friendCompetition.competition.compensation,
					friendCompetition.friendCompetitionStatus, friendCompetition.competitionOutputStatus,
					friendCompetition.competitionStatus)
			)
			.from(friend)
			.leftJoin(friendCompetition).on(friendCompetition.friend.id.eq(friend.id)).fetchJoin()
			.leftJoin(competition).on(competition.id.eq(friendCompetition.competition.id))
			.where(friend.users.id.eq(userId))
			.fetch();
	}

	@Override
	public List<FriendCompetition> getByUserId(Long userId) {
		return jpaQueryFactory.selectFrom(friendCompetition)
			.where(friendCompetition.friend.users.id.eq(userId))
			.fetch();
	}

	public BooleanExpression eqCompetitionId(Long competitionId) {
		if (competitionId == null) {
			return null;
		}
		return null;
	}
}
