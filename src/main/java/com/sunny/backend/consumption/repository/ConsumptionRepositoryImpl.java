package com.sunny.backend.consumption.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.sunny.backend.consumption.domain.QConsumption;
import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.consumption.domain.SpendType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.util.List;

import static com.sunny.backend.consumption.domain.QConsumption.consumption;
import static com.sunny.backend.user.domain.QUsers.users;

public class ConsumptionRepositoryImpl extends QuerydslRepositorySupport implements
    ConsumptionCustomRepository {

  private final JPAQueryFactory queryFactory;

  public ConsumptionRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    super(Consumption.class);
    this.queryFactory = jpaQueryFactory;
  }

  @Override
  public List<SpendTypeStatisticsResponse> getSpendTypeStatistics(Long userId) {
    QConsumption consumption = QConsumption.consumption;
    List<Tuple> tuples = queryFactory
        .select(
            consumption.category,
            consumption.name.count(),
            consumption.money.sum()
        )
        .from(consumption)
        .where(consumption.users.id.eq(userId))
        .groupBy(consumption.category)
        .fetch();

    long totalSpending = getTotalSpending(userId);
    return tuples.stream()
        .map(tuple -> {
          SpendType category = tuple.get(consumption.category);
          Long totalCount = tuple.get(consumption.name.count());
          Long totalMoney = tuple.get(consumption.money.sum());

          totalCount = totalCount != null ? totalCount : 0L;
          totalMoney = totalMoney != null ? totalMoney : 0L;
          double percentage = totalSpending != 0 ? (double) totalMoney / totalSpending * 100 : 0.0;
          BigDecimal percentageBigDecimal = new BigDecimal(percentage);

          return new SpendTypeStatisticsResponse(category, totalCount, totalMoney,
              percentageBigDecimal.setScale(1, RoundingMode.HALF_UP).doubleValue());
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


  private Long getTotalSpending(Long userId) {
    QConsumption consumption = QConsumption.consumption;
    Long totalSpending = queryFactory
        .select(consumption.money.sum())
        .from(consumption)
        .where(consumption.users.id.eq(userId))
        .fetchOne();
    return totalSpending != null ? totalSpending : 0L;
  }
}

