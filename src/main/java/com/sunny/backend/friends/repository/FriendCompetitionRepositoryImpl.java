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
import com.sunny.backend.user.domain.QUsers;

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
		QUsers users = QUsers.users;
		QUsers userFriend = QUsers.users;
		return jpaQueryFactory.select(
				Projections.constructor(FriendCompetitionDto.class, friend.id, friend.userFriend.id,
					friend.userFriend.nickname, friend.userFriend.profile, friend.status,
					competition.id, competition.message,
					competition.startDate, competition.endDate,
					competition.price, competition.compensation,
					friendCompetition.friendCompetitionStatus, friendCompetition.competitionOutputStatus)
			)
			.from(friend)
			.leftJoin(friendCompetition).on(friendCompetition.friend.id.eq(friend.id))
			.leftJoin(competition).on(competition.id.eq(friendCompetition.competition.id))
			.innerJoin(users).on(friend.users.id.eq(users.id))
			.innerJoin(userFriend).on(friend.userFriend.id.eq(userFriend.id))
			.where(users.id.eq(userId))
			.fetch();
	}

	@Override
	public List<FriendCompetition> getByUserId(Long userId) {
		return jpaQueryFactory.selectFrom(friendCompetition)
			.where(friendCompetition.friend.users.id.eq(userId))
			.fetch();
	}

	@Override
	public List<FriendCompetition> getByUserOrUserFriendByUserId(Long userId) {
		QFriend friend = QFriend.friend;
		return jpaQueryFactory.selectFrom(friendCompetition)
			.join(friend).on(friendCompetition.friend.id.eq(friend.id))
			.where(friend.users.id.eq(userId).or(friend.userFriend.id.eq(userId)))
			.fetch();
	}

	@Override
	public List<FriendCompetition> getByUserOrUserFriend(Long userId, Long userFriendId) {
		QFriend friend = QFriend.friend;
		// return jpaQueryFactory.selectFrom(friendCompetition)
		// 	.join(friend).on(friendCompetition.friend.id.eq(friend.id))
		// 	.where(friend.users.id.eq(userId),(friend.userFriend.id.eq(userId)))
		// 	.fetch();
		return null;
	}

	public BooleanExpression eqCompetitionId(Long competitionId) {
		if (competitionId == null) {
			return null;
		}
		return friendCompetition.competition.id.eq(competitionId);
	}
}
