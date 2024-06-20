package com.sunny.backend.community.repository;

import static com.sunny.backend.community.domain.QCommunity.*;
import static com.sunny.backend.user.domain.QUsers.*;
import static com.sunny.backend.user.domain.QUsersBlock.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.lang.Nullable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.domain.SortType;
import com.sunny.backend.community.dto.response.CommunityPageResponse;
import com.sunny.backend.user.domain.Users;

public class CommunityRepositoryImpl extends QuerydslRepositorySupport implements
	CommunityRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public CommunityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(Community.class);
		this.queryFactory = jpaQueryFactory;
	}

	public List<CommunityPageResponse> paginationNoOffsetBuilder(Users user, @Nullable Long communityId,
		SortType sortType, BoardType boardType, String searchText, int pageSize) {

		List<Users> blockedUsers = user.getBlockedUsers();

		BooleanExpression notBlockedUsers = community.users.notIn(blockedUsers);

		List<Community> results = queryFactory.selectFrom(community)
			.where(ltCommunityId(communityId), eqSearchText(searchText), eqBoardType(boardType), notBlockedUsers,
				community.id.notIn(queryFactory.select(community.id)
					.from(community)
					.join(users).on(community.users.id.eq(users.id))
					.join(usersBlock).on(users.id.eq(usersBlock.users.id))
					.where(usersBlock.blockedUser.id.eq(user.getId()))))
			.orderBy(sortType == SortType.VIEW_COUNT ? community.viewCnt.desc() : community.createdAt.desc())
			.limit(pageSize)
			.fetch();

		return results.stream()
			.map(community -> CommunityPageResponse.of(user, community))
			.toList();

	}

	private BooleanExpression ltCommunityId(Long communityId) {
		if (communityId == null) {
			return null;
		}
		return community.id.lt(communityId);
	}

	private BooleanExpression eqSearchText(String searchText) {
		if (searchText == null || searchText.isEmpty()) {
			return null;
		}

		return community.title.contains(searchText)
			.or(community.contents.contains(searchText))
			.or(community.users.nickname.contains(searchText));
	}

	private BooleanExpression eqBoardType(BoardType boardType) {
		if (boardType == BoardType.SAVING_TIPS) {
			return community.boardType.eq(BoardType.SAVING_TIPS);
		} else if (boardType == BoardType.FREE) {
			return community.boardType.eq(BoardType.FREE);
		}
		return null;
	}

}