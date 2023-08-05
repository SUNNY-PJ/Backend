package com.sunny.backend.repository.community;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.PageResponse;
import com.sunny.backend.entity.Community;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.sunny.backend.entity.QCommunity.community;

public class CommunityRepositoryImpl extends QuerydslRepositorySupport implements CommunityCustomRepository {
    private JPAQueryFactory queryFactory;

    public CommunityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Community.class);
        this.queryFactory = jpaQueryFactory;
    }


    @Override
    public List<PageResponse> findPageCommunity(Pageable pageable, String title, String contents) {
        return queryFactory.select( // 작성자 검색 조건 -> 추가
                Projections.bean(PageResponse.class,community.id,community.title,community.createdDate))
                .from(community)
                .where(likeTitle(title), likeContents(contents))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(community.createdDate.desc()) //default 값은 최신순?
                .fetch();

    }
    private BooleanExpression likeTitle(String title) {
        if(title == null || title.isEmpty()){
            return null;
        }
        return community.title.like(title);
    }

    private BooleanExpression likeContents(String contents) {
        if(contents == null || contents.isEmpty()){
            return null;
        }
        return community.contents.like(contents);
    }
}

