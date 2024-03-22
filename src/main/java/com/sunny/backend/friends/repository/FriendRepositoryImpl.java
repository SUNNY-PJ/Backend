package com.sunny.backend.friends.repository;

import static com.sunny.backend.competition.domain.QCompetition.*;
import static com.sunny.backend.friends.domain.QFriend.*;
import static com.sunny.backend.user.domain.QUsers.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.competition.dto.response.CompetitionResultDto;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.dto.response.FriendDto;

public class FriendRepositoryImpl extends QuerydslRepositorySupport implements FriendCustomRepository {
	private JPAQueryFactory queryFactory;

	public FriendRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(Friend.class);
		this.queryFactory = jpaQueryFactory;
	}

	// public BooleanExpression eqFriendStatus(FriendStatus friendStatus) {
	// 	if(friendStatus == null) {
	// 		return null;
	// 	}
	// 	return friend.friendStatus.eq(friendStatus);
	// }
	//
	// public BooleanExpression eqCompetitionStatus(CompetitionStatus competitionStatus) {
	// 	if(competitionStatus == null) {
	// 		return null;
	// 	}
	// 	return CompetitionStatus.competitionStatus.eq(competitionStatus);
	// }

	// @Override
	// public List<Friend> findFriends(Long userId, FriendStatus friendStatus, CompetitionStatus competitionStatus) {
	// 	return queryFactory.selectFrom(friend)
	// 		.leftJoin(users.friends, friend)
	// 		.leftJoin(friend.userFriend, users)
	// 		.leftJoin(friend.competition, competition)
	// 		.where(friend.users.id.eq(userId), eqFriendStatus(friendStatus), eqCompetitionStatus(competitionStatus))
	// 		.fetch();
	// }

	@Override
	public List<CompetitionResultDto> getCompetitionResult() {
		return null;
		// return queryFactory.select(
		// 		Projections.constructor(CompetitionResultDto.class, friend.users.id.as("userId"),
		// 			friend.userFriend.id.as("userFriendId"),
		// 			friend.userFriend.nickname.as("userFriendNickname"),
		// 			competition.output, competition.endDate, competition.price, competition.compensation)
		// 	)
		// 	.from(competition)
		// 	.join(friend).on(friend.competition.id.eq(competition.id))
		// 	.where(competition.endDate.eq(LocalDate.now()), competition.status.eq(FriendStatus.FRIEND))
		// 	.fetch();
	}
}
