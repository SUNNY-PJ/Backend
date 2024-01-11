package com.sunny.backend.community.repository;


import static com.sunny.backend.community.domain.QCommunity.community;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.domain.SortType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;



public class CommunityRepositoryImpl extends QuerydslRepositorySupport implements CommunityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommunityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Community.class);
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Slice<CommunityResponse.PageResponse> getCommunityList(Pageable pageable) {
        List<Community> results = queryFactory
            .selectFrom(community)
            .orderBy(community.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<CommunityResponse.PageResponse> dtoList = results.stream()
            .map(CommunityResponse.PageResponse::from)
            .collect(Collectors.toList());

        boolean hasNext = false;
        if (!dtoList.isEmpty() && dtoList.size() > pageable.getPageSize()) {
            dtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(dtoList, pageable, hasNext);
    }

    @Override
    public Slice<CommunityResponse.PageResponse> getPageListWithSearch(SortType sortType,
        BoardType boardType, String searchText, Pageable pageable) {
        JPAQuery<Community> query = queryFactory.selectFrom(community)
            .orderBy(sortType == SortType.VIEW ? community.view_cnt.desc()
                : community.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1);

        if (searchText != null) {
            query.where(eqSearchText(searchText));
        }
        if (boardType != null) {
            query.where(eqBoardType(boardType));
        }
        List<Community> results = query.fetch();

        List<CommunityResponse.PageResponse> dtoList = results.stream()
            .map(CommunityResponse.PageResponse::from)
            .collect(Collectors.toList());

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            dtoList.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(dtoList, pageable, hasNext);
    }


    private BooleanExpression eqSearchText(String searchText) {
        if (!searchText.isEmpty()) {
            return community.title.contains(searchText)
                .or(community.contents.contains(searchText))
                .or(community.users.name.contains(searchText));
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