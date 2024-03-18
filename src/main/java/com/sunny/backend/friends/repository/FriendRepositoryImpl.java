package com.sunny.backend.friends.repository;

import static com.sunny.backend.competition.domain.QCompetition.*;
import static com.sunny.backend.friends.domain.QFriend.*;
import static com.sunny.backend.user.domain.QUsers.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.competition.dto.response.CompetitionResultDto;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.Status;
import com.sunny.backend.friends.dto.response.FriendResponseDto;

public class FriendRepositoryImpl extends QuerydslRepositorySupport implements FriendCustomRepository {
	private JPAQueryFactory queryFactory;

	public FriendRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(Friend.class);
		this.queryFactory = jpaQueryFactory;
	}

	@Override
	public List<FriendResponseDto> getFriendResponse(Long userId) {
		return queryFactory.select(
				Projections.constructor(FriendResponseDto.class, friend.users.id.as("userId"),
					friend.id.as("friendId"), competition.id.as("competitionId"),
					friend.userFriend.id, friend.userFriend.nickname, friend.userFriend.profile,
					friend.status.as("friendStatus"),
					competition.status.as("competitionStatus"),
					competition.output
				))
			.from(users)
			.leftJoin(users.friends, friend)
			.leftJoin(friend.userFriend, users)
			.leftJoin(friend.competition, competition)
			.where(friend.users.id.eq(userId))
			.fetch();
	}

	@Override
	public List<CompetitionResultDto> getCompetitionResult() {
		return queryFactory.select(
				Projections.constructor(CompetitionResultDto.class, friend.users.id.as("userId"),
					friend.userFriend.id.as("userFriendId"),
					friend.userFriend.nickname.as("userFriendNickname"),
					competition.output, competition.endDate, competition.price, competition.compensation)
			)
			.from(competition)
			.join(friend).on(friend.competition.id.eq(competition.id))
			.where(competition.endDate.eq(LocalDate.now()), competition.status.eq(Status.APPROVE))
			.fetch();
	}
}
