package com.sunny.backend.repository.consumption;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.entity.QConsumption;
import com.sunny.backend.entity.SpendType;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

public class ConsumptionRepositoryImpl  extends QuerydslRepositorySupport implements ConsumptionCustomRepository {
    private JPAQueryFactory queryFactory;

    public ConsumptionRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Consumption.class);
        this.queryFactory = jpaQueryFactory;
    }
    @Override
    public List<SpendTypeStatisticsResponse> getSpendTypeStatistics() {
        QConsumption consumption = QConsumption.consumption;
        List<Tuple> tuples = queryFactory
                .select(
                        consumption.category,
                        consumption.name.count(),
                        consumption.money.sum()
                )
                .from(consumption)
                .groupBy(consumption.category)
                .fetch();

        // total 금액 계싼
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

    private long getTotalSpending() {
        QConsumption consumption = QConsumption.consumption;
        return queryFactory
                .select(consumption.money.sum())
                .from(consumption)
                .fetchOne();
    }
}


