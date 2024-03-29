package com.sunny.backend.community.repository;


import static com.sunny.backend.community.domain.QCommunity.community;
import static com.sunny.backend.user.domain.QUsers.users;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.community.dto.response.CommunityResponse;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.domain.SortType;
import com.sunny.backend.community.dto.response.CommunityResponse.PageResponse;
import com.sunny.backend.user.domain.Users;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;


public class CommunityRepositoryImpl extends QuerydslRepositorySupport implements
    CommunityRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  public CommunityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    super(Community.class);
    this.queryFactory = jpaQueryFactory;
  }
  public List<CommunityResponse.PageResponse> paginationNoOffsetBuilder(Users users,@Nullable Long communityId,
      SortType sortType, BoardType boardType, String searchText, int pageSize) {

    JPAQuery<Community> query = queryFactory.selectFrom(community)
        .where(ltCommunityId(communityId))
        .orderBy(sortType == SortType.VIEW ? community.viewCnt.desc()
            : community.createdAt.desc())
        .limit(pageSize);
    if (searchText != null) {
      query.where(eqSearchText(searchText));
    }
    if (boardType != null) {
      query.where(eqBoardType(boardType));
    }
    List<Community> results = query.fetch();
    return results.stream()
        .map(community -> PageResponse.from(users,community))
        .collect(Collectors.toList());
  }
  private BooleanExpression ltCommunityId(Long communityId) {
    if (communityId == null) {
      return null;
    }
    return community.id.lt(communityId);
  }
  private BooleanExpression eqSearchText(String searchText) {
    if (!searchText.isEmpty()) {
      return community.title.contains(searchText)
          .or(community.contents.contains(searchText))
          .or(community.users.nickname.contains(searchText));
    }
    return null;
  }
  private BooleanExpression eqBoardType(BoardType boardType) {
    if (boardType == BoardType.TIP) {
      return community.boardType.eq(BoardType.TIP);
    } else if (boardType == BoardType.FREE) {
      return community.boardType.eq(BoardType.FREE);
    }
    return null;
  }
}