package com.sunny.backend.repository.save;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.sunny.backend.entity.Save;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class SaveRepositoryCustomlmpl extends QuerydslRepositorySupport implements
    SaveCustomRepository {

  private JPAQueryFactory queryFactory;

  public SaveRepositoryCustomlmpl(JPAQueryFactory jpaQueryFactory) {
    super(Save.class);
    this.queryFactory = jpaQueryFactory;
  }
}
