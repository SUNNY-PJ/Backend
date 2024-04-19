package com.sunny.backend.community.repository;

import static com.sunny.backend.community.domain.QCommunity.*;

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

	public List<CommunityPageResponse> paginationNoOffsetBuilder(Users users, @Nullable Long communityId,
		SortType sortType, BoardType boardType, String searchText, int pageSize) {

		// List<Users> blockedUsers = blockList.stream()
		// 	.map(Block::getBlockedUser)
		// 	.toList();

		// List<Users> usersBlockList = userBlockList.stream()
		// 	.map(Block::getUser)
		// 	.toList();
		//
		// // Combine the two lists
		// List<Users> combinedBlockList = new ArrayList<>();
		// combinedBlockList.addAll(blockedUsers);
		// combinedBlockList.addAll(usersBlockList);

		// Use combinedBlockList in your BooleanExpression
		// BooleanExpression notBlockedUsers = community.users.notIn(combinedBlockList);
		List<Community> results = queryFactory.selectFrom(community)
			.where(ltCommunityId(communityId), eqSearchText(searchText), eqBoardType(boardType))
			.orderBy(sortType == SortType.VIEW ? community.viewCnt.desc() : community.createdAt.desc())
			.limit(pageSize)
			.fetch();

		return results.stream()
			.map(community -> CommunityPageResponse.of(users, community))
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
		if (boardType == BoardType.TIP) {
			return community.boardType.eq(BoardType.TIP);
		} else if (boardType == BoardType.FREE) {
			return community.boardType.eq(BoardType.FREE);
		}
		return null;
	}

	public List<Long> extractUserIds(List<CommunityPageResponse> communityResponses) {
		return communityResponses.stream()
			.map(CommunityPageResponse::userId)
			.toList();
	}
}