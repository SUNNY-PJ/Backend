package com.sunny.backend.repository.community;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.SearchType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public PageImpl<CommunityResponse.PageResponse> getCommunityList(Pageable pageable) {
        List<Community> results = queryFactory
                .selectFrom(community)
                .orderBy(community.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(community)
                .fetchCount();

        List<CommunityResponse.PageResponse> dtoList = results.stream()
                .map(CommunityResponse.PageResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, totalCount);
    }

    @Override
    public PageImpl<CommunityResponse.PageResponse> getPageListWithSearch(BoardType boardType, SearchType searchCondition, Pageable pageable){
        JPQLQuery<Community> query = queryFactory.select(community).from(community);

        //wherewjf
        BooleanBuilder whereClause = new BooleanBuilder();
        //whereClause 기준에 맞는 레코드만 출력
        whereClause.and(ContentMessageTitleEq(searchCondition.getContent(), searchCondition.getTitle()))
                .and(boardWriterEq(searchCondition.getWriter()));

        if (boardType == BoardType.자유) {
            whereClause.and(community.boardType.eq(BoardType.자유));
        } else if (boardType == BoardType.꿀팁) {
            whereClause.and(community.boardType.eq(BoardType.꿀팁));
        }

        query.where(whereClause).orderBy(community.createdDate.desc());

        List<Community> results = getQuerydsl().applyPagination(pageable, query).fetch();
        long totalCount = query.fetchCount();

        // 엔티티 -> Dto 매핑
        List<CommunityResponse.PageResponse> dtoList = results.stream()
                .map(CommunityResponse.PageResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, totalCount);

    }


    //제목 + 내용에 필요한 동적 쿼리문
    private BooleanExpression ContentMessageTitleEq(String communityContent,String communityTitle){
        // 글 내용 x, 글 제목 o
        if(!communityContent.isEmpty() && !communityTitle.isEmpty()){
            return community.title.contains(communityTitle).or(community.contents.contains(communityContent));
        }

        //글 내용 o, 글 제목 x
        if(!communityContent.isEmpty() && communityTitle.isEmpty()){
            return community.contents.contains(communityContent);
        }

        //글 제목 o
        if(communityContent.isEmpty() && !communityTitle.isEmpty()){
            return community.title.contains(communityTitle);
        }
        return null;
    }


    //  작성자 검색
    private BooleanExpression boardWriterEq(String boardWriter){
        if(boardWriter.isEmpty()){
            return null;
        }
        return community.writer.contains(boardWriter);
    }


}

