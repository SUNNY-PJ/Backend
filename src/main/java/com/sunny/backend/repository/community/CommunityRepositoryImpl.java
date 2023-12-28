package com.sunny.backend.repository.community;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.SortType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

import static com.sunny.backend.entity.QCommunity.community;

public class CommunityRepositoryImpl extends QuerydslRepositorySupport implements CommunityRepositoryCustom {
    private JPAQueryFactory queryFactory;

    public CommunityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Community.class);
        this.queryFactory = jpaQueryFactory;
    }


    @Override
    public Slice<CommunityResponse.PageResponse> getCommunityList(Pageable pageable) {
        List<Community> results = queryFactory
                .selectFrom(community)
                .orderBy(community.createdDate.desc()) // 기본 정렬은 최신순
                .offset(pageable.getOffset()) //시작점
                .limit(pageable.getPageSize()+1) //limit보다 데이터를 1개 더 갖고와서, 해당 데이터가 있다면 hasNext 변수에 true 값 넣어줌
                .fetch();

        List<CommunityResponse.PageResponse> dtoList = results.stream()
                .map(CommunityResponse.PageResponse::new)
                .collect(Collectors.toList());

        boolean hasNext = false;
        if (dtoList.size() > pageable.getPageSize()) {
            dtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(dtoList, pageable, hasNext);
    }

    @Override
    public Slice<CommunityResponse.PageResponse> getPageListWithSearch(SortType sortType, BoardType boardType, String searchText, Pageable pageable) {
        JPAQuery<Community> query = queryFactory.selectFrom(community)
                .orderBy(sortType == SortType.조회순 ? community.view_cnt.desc() : community.createdDate.desc())
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
                .map(CommunityResponse.PageResponse::new)
                .collect(Collectors.toList());

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            dtoList.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(dtoList, pageable, hasNext);
    }


    //제목 + 내용에 필요한 동적 쿼리문
    private BooleanExpression eqSearchText(String searchText) {
        if (!searchText.isEmpty()) {
            return community.title.contains(searchText)
                    .or(community.contents.contains(searchText))
                    .or(community.writer.contains(searchText));
        }
        return null;
    }

    private BooleanExpression eqBoardType(BoardType boardType) {
        if (boardType == BoardType.꿀팁) {
            return community.boardType.eq(BoardType.꿀팁);
        } else if (boardType == BoardType.자유) {
            return community.boardType.eq(BoardType.자유);
        }
        return null;
    }
}

