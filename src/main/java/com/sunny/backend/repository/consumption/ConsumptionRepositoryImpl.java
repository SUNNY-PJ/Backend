package com.sunny.backend.repository.consumption;

import static com.sunny.backend.entity.QConsumption.*;
import static com.sunny.backend.user.QUsers.*;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.entity.QConsumption;
import com.sunny.backend.entity.SpendType;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import net.bytebuddy.asm.Advice;

public class ConsumptionRepositoryImpl  extends QuerydslRepositorySupport implements ConsumptionCustomRepository {
    private JPAQueryFactory queryFactory;

    public ConsumptionRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Consumption.class);
        this.queryFactory = jpaQueryFactory;
    }
    @Override
    public List<SpendTypeStatisticsResponse> getSpendTypeStatistics() {
        QConsumption consumption = QConsumption.consumption;
        //튜플로 저장
        List<Tuple> tuples = queryFactory
                .select(
                        consumption.category,
                        consumption.name.count(),
                        consumption.money.sum()
                )
                .from(consumption)
                .groupBy(consumption.category)
                .fetch();

        // total 금액 계산
        long totalSpending = getTotalSpending();

        // 각 카테고리별로 계산
        return tuples.stream()
                .map(tuple -> {
                    SpendType category = tuple.get(consumption.category);
                    long totalCount = tuple.get(consumption.name.count());
                    long totalMoney = tuple.get(consumption.money.sum());
                    double percentage = (double) totalMoney / totalSpending * 100.0;

                    // Map the results to SpendTypeStatisticsResponse object
                    return new SpendTypeStatisticsResponse(category, totalCount, totalMoney, percentage);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long getComsumptionMoney(Long id, LocalDate startDate, LocalDate endDate) {
        return queryFactory.select(consumption.money.sum())
            .from(consumption)
            .join(users).on(users.id.eq(consumption.users.id))
            .where(consumption.dateField.between(startDate, endDate))
            .fetchOne();
    }

    private long getTotalSpending() {
        QConsumption consumption = QConsumption.consumption;
        return queryFactory
                .select(consumption.money.sum())
                .from(consumption)
                .fetchOne();
    }
}


