package com.sunny.backend.consumption.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.consumption.domain.QConsumption;
import com.sunny.backend.consumption.dto.response.ConsumptionResponse;
import com.sunny.backend.consumption.dto.response.SpendTypeStatisticsResponse;
import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.consumption.domain.SpendType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
  public List<SpendTypeStatisticsResponse> getSpendTypeStatistics(
      Long userId, Integer year,Integer month) {
    QConsumption consumption = QConsumption.consumption;

    List<SpendType> allCategories = List.of(SpendType.values());
    Map<SpendType, SpendTypeStatisticsResponse> categoryMap = new HashMap<>();
    for (SpendType category : allCategories) {
      categoryMap.put(category, new SpendTypeStatisticsResponse(
          category, 0L, 0L, 0.0));
    }
    List<Tuple> tuples = queryFactory
        .select(
            consumption.category,
            consumption.name.count(),
            consumption.money.sum()
        )
        .from(consumption)
        .where(
            consumption.users.id.eq(userId)
                .and(consumption.dateField.year().eq(year))
                .and(consumption.dateField.month().eq(month))
        )
        .groupBy(consumption.category)
        .fetch();
    long totalSpending = getTotalSpendingByYearMonth(userId,year,month);
    for (Tuple tuple : tuples) {
      SpendType category = tuple.get(consumption.category);
      Long totalCount = tuple.get(consumption.name.count());
      Long totalMoney = tuple.get(consumption.money.sum());

      totalCount = totalCount != null ? totalCount : 0L;
      totalMoney = totalMoney != null ? totalMoney : 0L;
      double percentage = totalSpending != 0 ? (double) totalMoney / totalSpending * 100 : 0.0;
      BigDecimal percentageBigDecimal = new BigDecimal(percentage);
      categoryMap.put(category, new SpendTypeStatisticsResponse(category, totalCount, totalMoney,
          percentageBigDecimal.setScale(1, RoundingMode.HALF_UP).doubleValue()));
    }
    return new ArrayList<>(categoryMap.values());
  }
  @Override
  public Long getComsumptionMoney(Long id, LocalDate startDate, LocalDate endDate) {
    return queryFactory.select(consumption.money.sum())
        .from(consumption)
        .join(users).on(users.id.eq(consumption.users.id))
        .where(consumption.dateField.between(startDate, endDate))
        .fetchOne();
  }
  private Long getTotalSpendingByYearMonth(Long userId, Integer year,Integer month) {
    QConsumption consumption = QConsumption.consumption;
    Long totalSpending = queryFactory
        .select(consumption.money.sum())
        .from(consumption)
        .where(
            consumption.users.id.eq(userId)
                .and(consumption.dateField.year().eq(year))
                .and(consumption.dateField.month().eq(month))
        )
        .fetchOne();
    return totalSpending != null ? totalSpending : 0L;
  }
  @Override
  public List<ConsumptionResponse.DetailConsumptionResponse> getConsumptionByCategory(
      Long userId, SpendType spendType, Integer year,Integer month) {
    QConsumption consumption = QConsumption.consumption;
    JPAQuery<Consumption> query = queryFactory
        .selectFrom(consumption)
        .where(
            consumption.users.id.eq(userId)
                .and(consumption.category.eq(spendType))
                .and(consumption.dateField.year().eq(year))
                .and(consumption.dateField.month().eq(month)
        ));
    List<Consumption> consumptionList = query.fetch();
    List<ConsumptionResponse.DetailConsumptionResponse> responseList = consumptionList.stream()
        .map(ConsumptionResponse.DetailConsumptionResponse::from)
        .toList();
    return responseList;
  }
}

