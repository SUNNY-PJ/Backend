package com.sunny.backend.friends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.competition.dto.response.CompetitionResultDto;
import com.sunny.backend.friends.domain.Friend;

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

	// @Override
	// public void updateCompetitionToNull(Long competitionId) {
	// 	queryFactory
	// 		.update(friend)
	// 		.set(friend.competition, (Competition)null)
	// 		.where(friend.competition.id.eq(competitionId))
	// 		.execute();
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
