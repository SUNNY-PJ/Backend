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
import java.util.List;

public class ConsumptionRepositoryImpl extends QuerydslRepositorySupport implements
    ConsumptionCustomRepository {

  private final JPAQueryFactory queryFactory;

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

    long totalSpending = getTotalSpending();
    return tuples.stream()
        .map(tuple -> {
          SpendType category = tuple.get(consumption.category);
          Long totalCount = tuple.get(consumption.name.count());
          Long totalMoney = tuple.get(consumption.money.sum());

          totalCount = totalCount != null ? totalCount : 0L;
          totalMoney = totalMoney != null ? totalMoney : 0L;

          double percentage =
              totalSpending != 0 ? (double) totalMoney / totalSpending * 100.0 : 0.0;
          return new SpendTypeStatisticsResponse(category, totalCount, totalMoney,
              (double) Math.round(percentage * 100) / 100);
        })
        .toList();
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
    Long totalSpending = queryFactory
        .select(consumption.money.sum())
        .from(consumption)
        .fetchOne();
    return totalSpending != null ? totalSpending : 0L;
  }
}

