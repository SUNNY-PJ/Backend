package com.sunny.backend.repository.friends;

import static com.sunny.backend.entity.friends.QFriends.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.entity.friends.ApproveType;
import com.sunny.backend.entity.friends.Friends;

public class FriendsRepositoryImpl extends QuerydslRepositorySupport implements FriendsRepositoryCustom {
	private JPAQueryFactory queryFactory;

	public FriendsRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(Friends.class);
		this.queryFactory = jpaQueryFactory;
	}

	@Override
	public List<FriendsResponse> getFindUserIdAndApproveType(Long userId, ApproveType approveType) {
		return queryFactory.select(
				Projections.constructor(FriendsResponse.class, friends.friendsSn, friends.friend.id.as("friendsId"),
					friends.friend.name, friends.friend.profile, friends.approve.as("approveType")))
			.from(friends)
			.where(friends.users.id.eq(userId), eqApproveType(approveType))
			.fetch();
	}

	private BooleanExpression eqApproveType(ApproveType approveType) {
		if (approveType == null) {
			return null;
		}
		switch (approveType) {
			case APPROVE -> {
				return friends.approve.eq(ApproveType.APPROVE);
			}
			case REFUSE -> {
				return friends.approve.eq(ApproveType.REFUSE);
			}
			case WAIT -> {
				return friends.approve.eq(ApproveType.WAIT);
			}
		}
		return null;
	}

}