package com.sunny.backend.repository.consumption;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.CommentResponse;
import com.sunny.backend.dto.response.ConsumptionResponse;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;

import com.sunny.backend.entity.Consumption;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sunny.backend.dto.response.CommentResponse.convertCommentToDto;
import static com.sunny.backend.entity.QComment.comment;
import static com.sunny.backend.entity.QConsumption.consumption;

public class ConsumptionRepositoryImpl  extends QuerydslRepositorySupport implements ConsumptionCustomRepository {
    private JPAQueryFactory queryFactory;

    public ConsumptionRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Consumption.class);
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public List<ConsumptionResponse> findByUsersId(Long id) {
        return  null;
    }
}
