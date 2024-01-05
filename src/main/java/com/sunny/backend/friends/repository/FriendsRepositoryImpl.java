package com.sunny.backend.friends.repository;

import static com.sunny.backend.friends.domain.QFriends.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.domain.Friends;

public class FriendsRepositoryImpl extends QuerydslRepositorySupport implements FriendsRepositoryCustom {
	private JPAQueryFactory queryFactory;

	public FriendsRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(Friends.class);
		this.queryFactory = jpaQueryFactory;
	}

	@Override
	public List<FriendsResponse> getFindUserIdAndApproveType(Long userId, FriendStatus friendStatus) {
		return queryFactory.select(
				Projections.constructor(FriendsResponse.class, friends.friendsSn, friends.friend.id.as("friendsId"),
					friends.friend.name, friends.friend.profile, friends.status.as("status")))
			.from(friends)
			.where(friends.users.id.eq(userId), eqApproveType(friendStatus))
			.fetch();
	}

	private BooleanExpression eqApproveType(FriendStatus friendStatus) {
		if (friendStatus == null) {
			return null;
		}
		switch (friendStatus) {
			case APPROVE -> {
				return friends.status.eq(FriendStatus.APPROVE);
			}
			case WAIT -> {
				return friends.status.eq(FriendStatus.WAIT);
			}
		}
		return null;
	}

}