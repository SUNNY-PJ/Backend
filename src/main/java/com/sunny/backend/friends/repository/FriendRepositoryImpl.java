package com.sunny.backend.friends.repository;

import static com.sunny.backend.competition.domain.QCompetition.*;
import static com.sunny.backend.friends.domain.QFriend.*;
import static com.sunny.backend.user.domain.QUsers.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.dto.response.FriendResponse;

public class FriendRepositoryImpl extends QuerydslRepositorySupport implements FriendCustomRepository {
	private JPAQueryFactory queryFactory;

	public FriendRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(Friend.class);
		this.queryFactory = jpaQueryFactory;
	}

	@Override
	public List<FriendResponse> getFriendResponse(Long userId) {
		return queryFactory.select(
				Projections.constructor(FriendResponse.class,
					friend.id.as("friendId"), competition.id.as("competitionId"),
					friend.userFriend.id, friend.userFriend.nickname, friend.userFriend.profile,
					friend.status.as("friendStatus"), competition.status.as("competitionStatus")
				))
			.from(users)
			.leftJoin(users.friends, friend)
			.leftJoin(friend.userFriend, users)
			.leftJoin(friend.competition, competition)
			.where(friend.users.id.eq(userId))
			.fetch();
	}
}
